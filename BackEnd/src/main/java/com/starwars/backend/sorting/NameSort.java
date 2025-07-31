package com.starwars.backend.sorting;

import com.starwars.backend.model.HasName;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.Comparator;

/**
 * SortStrategy for the "name" field.
 * Universal alphabetical sort, case-insensitive and null-safe.
 * Default fallback strategy for most resources.
 */
@Slf4j
@Component
public class NameSort implements SortStrategy<HasName> {

    @Override
    public String field() {
        // Used when the client requests sort=name (or defaults).
        return "name";
    }

    @Override
    public boolean supports(Class<?> type) {
        // Applies to anything with a getName() (People, Planets, etc).
        return HasName.class.isAssignableFrom(type);
    }

    @Override
    public Comparator<HasName> comparator() {
        // Alphabetical sort, case-insensitive with null values first.
        return Comparator.comparing(
                HasName::getName,
                Comparator.nullsFirst(String.CASE_INSENSITIVE_ORDER)
        );
    }
}
