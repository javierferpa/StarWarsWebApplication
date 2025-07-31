package com.starwars.backend.sorting;

import com.starwars.backend.model.HasGender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.Comparator;

/**
 * SortStrategy for the "gender" field.
 * Alphabetical sort, case-insensitive and null-safe.
 * Handles gender values for people sorting.
 */
@Slf4j
@Component
public class GenderSort implements SortStrategy<HasGender> {

    @Override
    public String field() {
        return "gender";
    }

    @Override
    public boolean supports(Class<?> type) {
        return HasGender.class.isAssignableFrom(type);
    }

    @Override
    public Comparator<HasGender> comparator() {
        // Alphabetical sort, case-insensitive with null values first.
        return Comparator.comparing(
                HasGender::getGender,
                Comparator.nullsFirst(String.CASE_INSENSITIVE_ORDER)
        );
    }
}
