package com.starwars.backend.sorting;

import com.starwars.backend.model.HasHeight;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.Comparator;

/**
 * SortStrategy for the "height" field.
 * - Handles string values like "unknown" by treating them as zero.
 * - Keeps things safe and robust, so if SWAPI data changes format, the UI doesn't crash.
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
        // Main comparator: safely parses height as int, using 0 as fallback for any errors.
        return Comparator.comparing(
                h -> parseOrZero(h.getHeight())
        );
    }

    /**
     * Helper to parse height as int.
     * - Returns 0 for "unknown", null, or any parse errors.
     * - I keep it forgiving on purpose, since SWAPI sometimes uses non-numeric values.
     */
    private static int parseOrZero(String raw) {
        try {
            return Integer.parseInt(raw);
        } catch (Exception e) {
            log.debug("Could not parse height '{}', defaulting to 0", raw);
            return 0;
        }
    }
}
