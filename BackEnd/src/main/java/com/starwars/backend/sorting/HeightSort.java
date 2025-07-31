package com.starwars.backend.sorting;

import com.starwars.backend.model.HasHeight;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.Comparator;

/**
 * Sorting strategy for height field values.
 * Handles non-numeric values like "unknown" by defaulting to zero.
 * Provides robust parsing to prevent UI crashes from unexpected data formats.
 */
@Slf4j
@Component
public class HeightSort implements SortStrategy<HasHeight> {

    @Override
    public String field() {
        // This strategy is only triggered for sort=height.
        return "height";
    }

    @Override
    public boolean supports(Class<?> type) {
        // This sort applies to any class that exposes height (e.g., PeopleDto).
        return HasHeight.class.isAssignableFrom(type);
    }

    @Override
    public Comparator<HasHeight> comparator() {
        // Safely parse height values, using zero for any non-numeric data
        return Comparator.comparing(
                h -> parseOrZero(h.getHeight())
        );
    }

    /**
     * Parses height string to integer with error handling.
     * Returns zero for "unknown", null, or invalid numeric values.
     * Handles comma-separated thousands for consistency with mass parsing.
     * Designed to be forgiving since SWAPI data may contain non-numeric entries.
     */
    private static int parseOrZero(String raw) {
        if (raw == null || raw.isBlank()) return 0;
        
        String cleaned = raw.trim();
        if ("unknown".equalsIgnoreCase(cleaned)) return 0;
        
        // Remove commas from thousands separator for consistency
        cleaned = cleaned.replace(",", "");
        
        try {
            return Integer.parseInt(cleaned);
        } catch (Exception e) {
            log.debug("Invalid height value '{}', using 0", raw);
            return 0;
        }
    }
}
