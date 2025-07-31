package com.starwars.backend.sorting;

import com.starwars.backend.model.HasMass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.Comparator;

/**
 * Sorting strategy for mass field values.
 * Handles non-numeric values like "unknown" by defaulting to zero.
 * Provides robust parsing to prevent UI crashes from unexpected data formats.
 */
@Slf4j
@Component
public class MassSort implements SortStrategy<HasMass> {

    @Override
    public String field() {
        // This strategy is only triggered for sort=mass.
        return "mass";
    }

    @Override
    public boolean supports(Class<?> type) {
        // This sort applies to any class that exposes mass (e.g., PeopleDto).
        return HasMass.class.isAssignableFrom(type);
    }

    @Override
    public Comparator<HasMass> comparator() {
        // Safely parse mass values, using zero for any non-numeric data
        return Comparator.comparing(
                m -> parseOrZero(m.getMass())
        );
    }

    /**
     * Parses mass string to double with error handling.
     * Returns zero for "unknown", null, or invalid numeric values.
     * Handles comma-separated thousands (e.g., "1,358" -> 1358.0).
     * Designed to be forgiving since SWAPI data may contain non-numeric entries.
     */
    private static double parseOrZero(String raw) {
        if (raw == null || raw.isBlank()) return 0.0;
        
        String cleaned = raw.trim();
        if ("unknown".equalsIgnoreCase(cleaned)) return 0.0;
        
        // Remove commas from thousands separator
        cleaned = cleaned.replace(",", "");
        
        try {
            return Double.parseDouble(cleaned);
        } catch (Exception e) {
            log.debug("Invalid mass value '{}', using 0", raw);
            return 0.0;
        }
    }
}
