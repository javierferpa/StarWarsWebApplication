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
 * HTTP client for Star Wars API integration.
 * Handles paginated responses with fallback to flat array endpoints when needed.
 * Supports both People and Planets resources with consistent error handling.
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

    /** Fetch a single paginated page of People */
    public Mono<SwapiPagedResponse<PeopleDto>> fetchPeoplePage(int page, String search) {
        log.debug("Fetching people page: {} (search: '{}')", page, search);
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
     * Follow pagination URL for People resources
     * Normalizes URL to prevent duplicate /api/ path segments
     */
    public Mono<SwapiPagedResponse<PeopleDto>> fetchPeopleByAbsoluteUrl(String nextUrl) {
        String normalized = normalizeNext(nextUrl);
        log.debug("Following people pagination: '{}' -> '{}'", nextUrl, normalized);
        return swapiWebClient
                .get()
                .uri(normalized)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<SwapiPagedResponse<PeopleDto>>() {});
    }

    /**
     * Retrieve all People records using pagination
     * Falls back to flat array endpoint if pagination fails
     */
    public Mono<List<PeopleDto>> fetchAllPeople(String search) {
        log.info("Fetching all people data (search: '{}')", search);
        return fetchAll(
                search,
                page -> fetchPeoplePage(page, search),
                this::fetchPeopleByAbsoluteUrl,
                () -> fetchPeopleArrayFallback(search)
        );
    }

    /**
     * Fallback endpoint for People when pagination is not available
     * Performs client-side filtering by name
     */
    private Mono<List<PeopleDto>> fetchPeopleArrayFallback(String search) {
        log.warn("Using fallback endpoint for people (search: '{}')", search);
        return swapiWebClient
                .get()
                .uri(uri -> uri.path("/people/").build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<PeopleDto>>() {})
                .map(list -> filterByName(list, search, PeopleDto::getName))
                .doOnSuccess(list -> log.info("Fallback endpoint returned {} people (search: '{}')", list.size(), search))
                .onErrorResume(WebClientResponseException.class, ex -> {
                    log.error("People fallback failed - status: {}, response: {}",
                            ex.getStatusCode(), ex.getResponseBodyAsString(), ex);
                    return Mono.just(List.of());
                });
    }

    // ---------- PLANETS ----------

    /** Fetch a single paginated page of Planets */
    public Mono<SwapiPagedResponse<PlanetDto>> fetchPlanetsPage(int page, String search) {
        log.debug("Fetching planets page: {} (search: '{}')", page, search);
        return swapiWebClient
                .get()
                .uri(uri -> uri.path("/planets/")
                        .queryParam("page", page)
                        .queryParamIfPresent("search", Optional.ofNullable(search).filter(s -> !s.isBlank()))
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<SwapiPagedResponse<PlanetDto>>() {});
    }

    /** Follow pagination URL for Planets resources */
    public Mono<SwapiPagedResponse<PlanetDto>> fetchPlanetsByAbsoluteUrl(String nextUrl) {
        String normalized = normalizeNext(nextUrl);
        log.debug("Following planets pagination: '{}' -> '{}'", nextUrl, normalized);
        return swapiWebClient
                .get()
                .uri(normalized)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<SwapiPagedResponse<PlanetDto>>() {});
    }

    /** Retrieve all Planets records using pagination with fallback support */
    public Mono<List<PlanetDto>> fetchAllPlanets(String search) {
        log.info("Fetching all planets data (search: '{}')", search);
        return fetchAll(
                search,
                page -> fetchPlanetsPage(page, search),
                this::fetchPlanetsByAbsoluteUrl,
                () -> fetchPlanetsArrayFallback(search)
        );
    }

    /** Fallback endpoint for Planets when pagination is not available */
    private Mono<List<PlanetDto>> fetchPlanetsArrayFallback(String search) {
        log.warn("Using fallback endpoint for planets (search: '{}')", search);
        return swapiWebClient
                .get()
                .uri(uri -> uri.path("/planets/").build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<PlanetDto>>() {})
                .map(list -> filterByName(list, search, PlanetDto::getName))
                .doOnSuccess(list -> log.info("Fallback endpoint returned {} planets (search: '{}')", list.size(), search))
                .onErrorResume(WebClientResponseException.class, ex -> {
                    log.error("Planets fallback failed - status: {}, response: {}",
                            ex.getStatusCode(), ex.getResponseBodyAsString(), ex);
                    return Mono.just(List.of());
                });
    }

    // ---------- GENERIC PAGINATION ROUTINE ----------

    /**
     * Generic pagination handler for SWAPI resources
     * Follows 'next' links until exhausted with safety limits
     * Falls back to flat array endpoint on pagination failures
     */
    private <T> Mono<List<T>> fetchAll(String search,
                                       Function<Integer, Mono<SwapiPagedResponse<T>>> firstPageFetcher,
                                       Function<String, Mono<SwapiPagedResponse<T>>> byUrlFetcher,
                                       Supplier<Mono<List<T>>> flatArrayFallback) {

        return firstPageFetcher.apply(1)
                .doOnNext(resp -> log.debug("Retrieved page 1: {} items, next: {}", safeSize(resp), resp.getNext()))
                .expand(resp -> {
                    String next = resp.getNext();
                    if (next == null) {
                        log.debug("Pagination complete");
                        return Mono.empty();
                    }
                    String normalized = normalizeNext(next);
                    log.debug("Fetching next page: '{}' -> '{}'", next, normalized);
                    return byUrlFetcher.apply(normalized)
                            .doOnNext(r -> log.debug("Retrieved page: {} items, next: {}", safeSize(r), r.getNext()));
                })
                .take(MAX_PAGES)
                .flatMapIterable(SwapiPagedResponse::getResults)
                .collectList()
                .doOnSuccess(list -> log.info("Pagination complete - {} total records (search: '{}')", list.size(), search))
                .onErrorResume(ex -> {
                    if (isPagedFormatProblem(ex)) {
                        log.warn("Pagination failed, trying fallback - reason: {}", ex.toString());
                        return flatArrayFallback.get();
                    }
                    log.error("Unexpected pagination error", ex);
                    return Mono.error(ex);
                })
                .timeout(DEFAULT_TIMEOUT);
    }

    // ---------- HELPERS ----------

    /**
     * Normalize pagination URLs from SWAPI responses
     * Prevents duplicate /api/ path segments in URLs
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
     * Decides if the error indicates pagination format issues where fallback is appropriate.
     * Treats decoding issues as fallback triggers to ensure users receive data when possible.
     */
    private static boolean isPagedFormatProblem(Throwable ex) {
        if (ex instanceof WebClientResponseException wce) {
            HttpStatusCode code = wce.getStatusCode();
            return code.is4xxClientError() || code.is5xxServerError();
        }
        return true;
    }

    /**
     * Generic name filtering utility for different entity types.
     * Uses function parameter to extract name field from any object type.
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
