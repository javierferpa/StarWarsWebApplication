package com.starwars.backend.sorting;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

/**
 * Central point for sorting logic in the backend.
 * My design here:
 * - The engine receives a list of registered SortStrategies at startup (injected by Spring).
 * - For every sort request, it tries to find a strategy matching both the requested field and the DTO type.
 * - If the field isn't recognized, we keep original order (comparator returns 0).
 * This allows me to plug in new sort strategies (by field or type) without touching this core class.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SortEngine {

    private final List<SortStrategy<?>> strategies;

    /**
     * Sorts a list of objects by the requested field and direction.
     *
     * @param data   the items to sort (already filtered)
     * @param type   the DTO class (e.g., PeopleDto.Class)
     * @param field  which field to sort by (e.g., "name", "population", ...)
     * @param asc    true for ascending, false for descending
     * @return       a sorted copy of the input list
     */
    public <T> List<T> sort(List<T> data, Class<T> type, String field, boolean asc) {
        // Try to find a registered SortStrategy for this field and type.
        @SuppressWarnings("unchecked")
        SortStrategy<T> strategy = (SortStrategy<T>) strategies.stream()
                .filter(s -> s.field().equalsIgnoreCase(field))
                .filter(s -> s.supports(type))
                .findFirst()
                .orElse(null);

        Comparator<T> comp;
        if (strategy != null) {
            comp = strategy.comparator();
            log.debug("Sorting {} items by '{}' using [{}], direction={}",
                    data.size(), field, strategy.getClass().getSimpleName(), asc ? "ASC" : "DESC");
        } else {
            // No recognized strategy: keep original order.
            log.warn("Unknown sort field '{}' for type {}: keeping original order.", field, type.getSimpleName());
            comp = (a, b) -> 0;
        }

        if (!asc) {
            comp = comp.reversed();
        }

        return data.stream().sorted(comp).toList();
    }
}
