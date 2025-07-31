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
        return "created";
    }

    @Override
    public boolean supports(Class<?> type) {
        return HasCreated.class.isAssignableFrom(type);
    }

    @Override
    public Comparator<HasCreated> comparator() {
        // Sort by creation date with null values placed first for consistency
        // Null-safe comparison prevents NPE and handles incomplete upstream data
        return Comparator.comparing(
                HasCreated::getCreated,
                Comparator.nullsFirst(Comparator.naturalOrder())
        );
    }
}
