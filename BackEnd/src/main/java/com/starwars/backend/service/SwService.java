package com.starwars.backend.service;

import com.starwars.backend.model.PageDto;
import com.starwars.backend.model.PeopleDto;
import com.starwars.backend.model.PlanetDto;
import com.starwars.backend.sorting.SortEngine;
import com.starwars.backend.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * This service acts as a thin façade between controllers and the cache + sorting layer.
 * My goal with this class:
 * - Keep business logic here as flat as possible (only coordination, no sorting details).
 * - Enforce consistent defaults for sorting (always fall back to "name" ASC if sort not provided).
 * - Make it easy to add resource-specific tweaks later, since all logic goes through this service.
 * The controller never talks to SortEngine or the cache directly—only through here.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SwService {

    private final SwCacheService cacheService;
    private final SortEngine sortEngine;

    // ---------------- PEOPLE ----------------

    /**
     * Returns one page of People.
     * I always log the request params for observability.
     * If no sort field is provided, I default to "name" ASC.
     */
    public PageDto<PeopleDto> getPeople(int page, int size, String search, String sort, String dir) {
        log.info("getPeople(page={}, size={}, search='{}', sort='{}', dir='{}')",
                page, size, search, sort, dir);

        List<PeopleDto> all = cacheService.loadAllPeople(search);
        log.debug("Loaded {} people from cache (post-search).", all.size());

        PageDto<PeopleDto> result = fetchPage(all, PeopleDto.class, page, size, sort, dir);

        log.info("getPeople -> returned {} items (total={}, page={}, size={})",
                result.getItems().size(), result.getTotal(), result.getPage(), result.getSize());
        return result;
    }

    // ---------------- PLANETS ----------------

    /**
     * Returns one page of Planets.
     * Same default policy: fallback to "name" ASC if no sort given.
     */
    public PageDto<PlanetDto> getPlanets(int page, int size, String search, String sort, String dir) {
        log.info("getPlanets(page={}, size={}, search='{}', sort='{}', dir='{}')",
                page, size, search, sort, dir);

        List<PlanetDto> all = cacheService.loadAllPlanets(search);
        log.debug("Loaded {} planets from cache (post-search).", all.size());

        PageDto<PlanetDto> result = fetchPage(all, PlanetDto.class, page, size, sort, dir);

        log.info("getPlanets -> returned {} items (total={}, page={}, size={})",
                result.getItems().size(), result.getTotal(), result.getPage(), result.getSize());
        return result;
    }

    // ---------------- SHARED / GENERIC ----------------

    /**
     * Main helper for sorting and pagination.
     * - If sort is blank/null → default to "name".
     * - Direction is ASC unless dir=desc.
     * - Logs all key steps for traceability during troubleshooting.
     * By centralizing defaults here (not in SortEngine), I keep the option open to customize per-entity
     * if requirements change.
     */
    private <T> PageDto<T> fetchPage(List<T> items,
                                     Class<T> type,
                                     int page,
                                     int size,
                                     String sort,
                                     String dir) {
        boolean sortBlank = (sort == null || sort.isBlank());
        String sortField = sortBlank ? "name" : sort;
        boolean ascending = !"desc".equalsIgnoreCase(dir);

        log.debug("Sorting {} items by '{}' ({}) [defaultApplied={}].",
                items.size(), sortField, ascending ? "ASC" : "DESC", sortBlank);

        List<T> sorted = sortEngine.sort(items, type, sortField, ascending);

        PageDto<T> pageDto = PaginationUtil.paginate(sorted, page, size);

        log.debug("Pagination -> page={}, size={}, total={}, returned={}",
                pageDto.getPage(), pageDto.getSize(), pageDto.getTotal(), pageDto.getItems().size());

        return pageDto;
    }
}
