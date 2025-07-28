package com.starwars.backend.sorting;

import com.starwars.backend.model.HasCreated;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.Comparator;

/**
 * SortStrategy for the "created" timestamp.
 * - Treats null values as earlier (appear first in ASC order).
 * - Applies to any DTO with a created timestamp (PeopleDto, PlanetDto, etc.).
 *
 * In practice: if SWAPI data is missing the field, it doesn't break the sort order.
 */
@Slf4j
@Component
public class CreatedSort implements SortStrategy<HasCreated> {

    @Override
    public String field() {
        // This strategy is triggered by sort=created.
        return "created";
    }

    @Override
    public boolean supports(Class<?> type) {
        // Works for any DTO that exposes a created timestamp.
        return HasCreated.class.isAssignableFrom(type);
    }

    @Override
    public Comparator<HasCreated> comparator() {
        // Main comparator: sorts by created (nulls come first, for consistency).
        // Comparator.nullsFirst: guarantees no NPEs and sensible order even if upstream data is messy.
        return Comparator.comparing(
                HasCreated::getCreated,
                Comparator.nullsFirst(Comparator.naturalOrder())
        );
    }
}
