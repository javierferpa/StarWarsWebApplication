package com.starwars.backend.client;

import com.starwars.backend.model.PeopleDto;
import com.starwars.backend.model.PlanetDto;
import com.starwars.backend.model.SwapiPagedResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Thin, resilient client around the swapi.info mirror.
 * My approach here:
 * - I first try the paginated endpoints and keep following 'next' until it returns null.
 * - If paged decoding fails (e.g., flat array returned), I fall back to a non-paged endpoint.
 * - This client is generic so I can reuse the paging logic for both People and Planets.
 * - I include logging at key points so I can trace production issues and easily explain design choices.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SwapiClient {

    /** Safety guard to avoid accidental infinite loops from a misbehaving upstream. */
    private static final int MAX_PAGES = 50;

    /** Timeout for any fetch chain; avoids hanging on slow or stalled upstream. */
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);

    private final WebClient swapiWebClient;

    // ---------- PEOPLE ----------

    /** Fetch a single paginated page of People. */
    public Mono<SwapiPagedResponse<PeopleDto>> fetchPeoplePage(int page, String search) {
        log.debug("Fetching People page={} search='{}'", page, search);
        return swapiWebClient
                .get()
                .uri(uri -> uri.path("/people/")
                        .queryParam("page", page)
                        .queryParamIfPresent("search", Optional.ofNullable(search).filter(s -> !s.isBlank()))
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<SwapiPagedResponse<PeopleDto>>() {});
    }

    /**
     * Follow the next URL returned by SWAPI pagination.
     * I normalize it so that if the baseUrl already contains `/api`, I don't end up with `/api/api/...`.
     */
    public Mono<SwapiPagedResponse<PeopleDto>> fetchPeopleByAbsoluteUrl(String nextUrl) {
        String normalized = normalizeNext(nextUrl);
        log.debug("Fetching People by nextURL='{}' (normalized='{}')", nextUrl, normalized);
        return swapiWebClient
                .get()
                .uri(normalized)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<SwapiPagedResponse<PeopleDto>>() {});
    }

    /**
     * Fetch all People:
     * - Start at page 1, keep following 'next' until null
     * - If paged endpoint fails, try fallback
     */
    public Mono<List<PeopleDto>> fetchAllPeople(String search) {
        log.info("Fetching ALL People (search='{}')", search);
        return fetchAll(
                search,
                page -> fetchPeoplePage(page, search),
                this::fetchPeopleByAbsoluteUrl,
                () -> fetchPeopleArrayFallback(search)
        );
    }

    /**
     * Fallback for People if the endpoint returns a flat array (no pagination).
     * Local filter by name for consistency.
     */
    private Mono<List<PeopleDto>> fetchPeopleArrayFallback(String search) {
        log.warn("Falling back to flat People array (search='{}')", search);
        return swapiWebClient
                .get()
                .uri(uri -> uri.path("/people/").build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<PeopleDto>>() {})
                .map(list -> filterByName(list, search, PeopleDto::getName))
                .doOnSuccess(list -> log.info("People fallback returned {} items (search='{}')", list.size(), search))
                .onErrorResume(WebClientResponseException.class, ex -> {
                    log.error("People fallback failed: status={} body={}",
                            ex.getStatusCode(), ex.getResponseBodyAsString(), ex);
                    return Mono.just(List.of());
                });
    }

    // ---------- PLANETS ----------

    /** Fetch a single paginated page of Planets. */
    public Mono<SwapiPagedResponse<PlanetDto>> fetchPlanetsPage(int page, String search) {
        log.debug("Fetching Planets page={} search='{}'", page, search);
        return swapiWebClient
                .get()
                .uri(uri -> uri.path("/planets/")
                        .queryParam("page", page)
                        .queryParamIfPresent("search", Optional.ofNullable(search).filter(s -> !s.isBlank()))
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<SwapiPagedResponse<PlanetDto>>() {});
    }

    /** Follow the next URL for Planets pagination (same normalization as People). */
    public Mono<SwapiPagedResponse<PlanetDto>> fetchPlanetsByAbsoluteUrl(String nextUrl) {
        String normalized = normalizeNext(nextUrl);
        log.debug("Fetching Planets by nextURL='{}' (normalized='{}')", nextUrl, normalized);
        return swapiWebClient
                .get()
                .uri(normalized)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<SwapiPagedResponse<PlanetDto>>() {});
    }

    /** Fetch all Planets (paginated or fallback, just like People). */
    public Mono<List<PlanetDto>> fetchAllPlanets(String search) {
        log.info("Fetching ALL Planets (search='{}')", search);
        return fetchAll(
                search,
                page -> fetchPlanetsPage(page, search),
                this::fetchPlanetsByAbsoluteUrl,
                () -> fetchPlanetsArrayFallback(search)
        );
    }

    /** Fallback for Planets if endpoint returns a flat array. */
    private Mono<List<PlanetDto>> fetchPlanetsArrayFallback(String search) {
        log.warn("Falling back to flat Planets array (search='{}')", search);
        return swapiWebClient
                .get()
                .uri(uri -> uri.path("/planets/").build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<PlanetDto>>() {})
                .map(list -> filterByName(list, search, PlanetDto::getName))
                .doOnSuccess(list -> log.info("Planets fallback returned {} items (search='{}')", list.size(), search))
                .onErrorResume(WebClientResponseException.class, ex -> {
                    log.error("Planets fallback failed: status={} body={}",
                            ex.getStatusCode(), ex.getResponseBodyAsString(), ex);
                    return Mono.just(List.of());
                });
    }

    // ---------- GENERIC PAGINATION ROUTINE ----------

    /**
     * Core pagination logic for both People and Planets:
     * - Start with page 1 (or custom fetcher)
     * - expand() keeps following 'next' until it's null
     * - MAX_PAGES is a hard safety cap
     * - If anything fails (decoding, HTTP), I try the fallback supplier
     * Logging at each key step, so I can debug production issues and answer questions in an interview.
     */
    private <T> Mono<List<T>> fetchAll(String search,
                                       Function<Integer, Mono<SwapiPagedResponse<T>>> firstPageFetcher,
                                       Function<String, Mono<SwapiPagedResponse<T>>> byUrlFetcher,
                                       Supplier<Mono<List<T>>> flatArrayFallback) {

        return firstPageFetcher.apply(1)
                .doOnNext(resp -> log.debug("Fetched page 1: items={}, next={}", safeSize(resp), resp.getNext()))
                .expand(resp -> {
                    String next = resp.getNext();
                    if (next == null) {
                        log.debug("No more pages. Pagination finished.");
                        return Mono.empty();
                    }
                    String normalized = normalizeNext(next);
                    log.debug("Following next='{}' (normalized='{}')", next, normalized);
                    return byUrlFetcher.apply(normalized)
                            .doOnNext(r -> log.debug("Fetched next page: items={}, next={}", safeSize(r), r.getNext()));
                })
                .take(MAX_PAGES)
                .flatMapIterable(SwapiPagedResponse::getResults)
                .collectList()
                .doOnSuccess(list -> log.info("Fetched {} items from paginated endpoint (search='{}')", list.size(), search))
                .onErrorResume(ex -> {
                    // If paginated fetch fails, fallback to flat array endpoint for max robustness
                    if (isPagedFormatProblem(ex)) {
                        log.warn("Paged fetch failed, switching to flat-array fallback. Cause: {}", ex.toString());
                        return flatArrayFallback.get();
                    }
                    log.error("Unexpected error while fetching paginated data", ex);
                    return Mono.error(ex);
                })
                .timeout(DEFAULT_TIMEOUT);
    }

    // ---------- HELPERS ----------

    /**
     * Normalize 'next' URLs returned by the SWAPI.
     * - If absolute, return as is
     * - If starts with '/api/', trim so it doesn't duplicate '/api' (my baseUrl already ends with /api)
     */
    private static String normalizeNext(String next) {
        if (next == null) return null;
        String lower = next.toLowerCase();
        if (lower.startsWith("http://") || lower.startsWith("https://")) {
            return next;
        }
        // Avoid '/api/api/...' duplication
        return next.startsWith("/api/") ? next.substring(4) : next;
    }

    /** Safe size helper for paged responses. */
    private static int safeSize(SwapiPagedResponse<?> r) {
        return r.getResults() == null ? 0 : r.getResults().size();
    }

    /**
     * Decides if the error means the paged shape is missing or broken, in which case fallback makes sense.
     * I purposely treat decoding issues as fallback triggers, since users want *something* rather than nothing.
     */
    private static boolean isPagedFormatProblem(Throwable ex) {
        if (ex instanceof WebClientResponseException wce) {
            HttpStatusCode code = wce.getStatusCode();
            return code.is4xxClientError() || code.is5xxServerError();
        }
        return true;
    }

    /**
     * Simple in-memory filtering by name.
     * I pass in the extractor function so it works for both People and Planets without forcing an interface.
     */
    private static <T> List<T> filterByName(List<T> list, String search, java.util.function.Function<T, String> nameExtractor) {
        if (search == null || search.isBlank()) return list;
        String needle = search.toLowerCase();
        return list.stream()
                .filter(item -> {
                    String name = nameExtractor.apply(item);
                    return name != null && name.toLowerCase().contains(needle);
                })
                .toList();
    }
}
