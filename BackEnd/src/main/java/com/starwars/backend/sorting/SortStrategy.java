package com.starwars.backend.sorting;

import java.util.Comparator;

public interface SortStrategy<T> {
    String field();
    boolean supports(Class<?> type);
    Comparator<T> comparator();
}

