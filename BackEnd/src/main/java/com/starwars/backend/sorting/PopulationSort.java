package com.starwars.backend.sorting;

import com.starwars.backend.model.HasPopulation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.Comparator;
import java.util.Optional;

/**
 * Sorting strategy for population field values.
 * Handles null values by treating them as zero for consistent ordering.
 * Designed to be forgiving since upstream data may contain "unknown" or missing values.
 */
@Slf4j
@Component
public class PopulationSort implements SortStrategy<HasPopulation> {

    @Override
    public String field() {
        return "population";
    }

    @Override
    public boolean supports(Class<?> type) {
        return HasPopulation.class.isAssignableFrom(type);
    }

    @Override
    public Comparator<HasPopulation> comparator() {
        // Sorts by population, treating null as zero.
        // The Optional ensures we never get a NullPointerException on weird data.
        return Comparator.comparing(
                item -> Optional.ofNullable(item.getPopulation()).orElse(0L)
        );
    }
}
