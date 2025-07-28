package com.starwars.backend.sorting;

import com.starwars.backend.model.HasPopulation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.Comparator;
import java.util.Optional;

/**
 * SortStrategy for the "population" field.
 * - Null values are treated as zero, so missing/unknown populations are always sorted to the bottom/top depending on order.
 * - I keep this forgiving since the upstream data sometimes uses 'unknown' or missing values.
 */
@Slf4j
@Component
public class PopulationSort implements SortStrategy<HasPopulation> {

    @Override
    public String field() {
        // Used when the client requests sort=population.
        return "population";
    }

    @Override
    public boolean supports(Class<?> type) {
        // Applies to anything with a getPopulation() method (e.g., PlanetDto).
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
