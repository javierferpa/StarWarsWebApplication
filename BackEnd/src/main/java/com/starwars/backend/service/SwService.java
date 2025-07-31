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
 * Service layer facade for Star Wars data operations.
 * Coordinates between cache service and sorting engine while enforcing consistent defaults.
 * Handles request logging and response formatting for both People and Planets endpoints.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SwService {

    private final SwCacheService cacheService;
    private final SortEngine sortEngine;

    // ---------------- PEOPLE ----------------

    /**
     * Returns paginated People data with optional search and sorting.
     * Logs request parameters for observability and debugging.
     * Defaults to name-based ascending sort when no sort field is specified.
     */
    public PageDto<PeopleDto> getPeople(int page, int size, String search, String sort, String dir) {
        log.info("Request: getPeople(page={}, size={}, search='{}', sort='{}', dir='{}')",
                page, size, search, sort, dir);

        List<PeopleDto> all = cacheService.loadAllPeople(search);
        log.debug("Loaded {} people from cache", all.size());

        PageDto<PeopleDto> result = fetchPage(all, PeopleDto.class, page, size, sort, dir);

        log.info("Response: {} items (total={}, page={}, size={})",
                result.getItems().size(), result.getTotal(), result.getPage(), result.getSize());
        return result;
    }

    // ---------------- PLANETS ----------------

    /**
     * Returns paginated Planets data with optional search and sorting.
     * Uses same default sorting policy as People: fallback to name ascending.
     */
    public PageDto<PlanetDto> getPlanets(int page, int size, String search, String sort, String dir) {
        log.info("Request: getPlanets(page={}, size={}, search='{}', sort='{}', dir='{}')",
                page, size, search, sort, dir);

        List<PlanetDto> all = cacheService.loadAllPlanets(search);
        log.debug("Loaded {} planets from cache", all.size());

        PageDto<PlanetDto> result = fetchPage(all, PlanetDto.class, page, size, sort, dir);

        log.info("Response: {} items (total={}, page={}, size={})",
                result.getItems().size(), result.getTotal(), result.getPage(), result.getSize());
        return result;
    }

    // ---------------- SHARED / GENERIC ----------------

    /**
     * Core sorting and pagination logic shared between People and Planets.
     * Applies default sorting by name when no field is specified.
     * Centralizes default behavior while maintaining flexibility for entity-specific customization.
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

        log.debug("Sorting {} items by '{}' ({}) [default applied: {}]",
                items.size(), sortField, ascending ? "ASC" : "DESC", sortBlank);

        List<T> sorted = sortEngine.sort(items, type, sortField, ascending);

        PageDto<T> pageDto = PaginationUtil.paginate(sorted, page, size);

        log.debug("Paginated {} items -> page={}, size={}, total={}",
                pageDto.getItems().size(), pageDto.getPage(), pageDto.getSize(), pageDto.getTotal());

        return pageDto;
    }
}
