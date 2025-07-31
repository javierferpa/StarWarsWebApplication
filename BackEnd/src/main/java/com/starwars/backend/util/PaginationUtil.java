package com.starwars.backend.util;

import com.starwars.backend.model.PageDto;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Utility class for paginating lists into PageDto objects.
 * Uses zero-based page indexing where page=0 represents the first page.
 * Generic implementation works with any data type for flexible reuse.
 */
@RequiredArgsConstructor
public final class PaginationUtil {

    /**
     * Paginates a full list into a PageDto, enforcing safe bounds.
     *
     * @param items full list of T (already filtered/sorted)
     * @param page  zero-based page index (so 0 = first page)
     * @param size  number of items per page (enforced to minimum 1)
     * @param <T>   type of item in the list
     * @return      a PageDto containing only the requested slice, with paging metadata
     */
    public static <T> PageDto<T> paginate(List<T> items, int page, int size) {
        int total = items.size();

        // Defensive: don't allow negative pages or sizes.
        int validPage = Math.max(page, 0);
        int validSize = Math.max(size, 1);
        int from = validPage * validSize;

        // If page is out of range, return empty items but echo the original page/size for traceability.
        if (from >= total) {
            return PageDto.<T>builder()
                    .page(page)
                    .size(size)
                    .total(total)
                    .items(List.of())
                    .build();
        }

        // Calculate the end index safely (never overflow).
        int to = Math.min(from + validSize, total);
        List<T> slice = items.subList(from, to);

        // Return the page slice and metadata.
        return PageDto.<T>builder()
                .page(page)
                .size(size)
                .total(total)
                .items(slice)
                .build();
    }
}
