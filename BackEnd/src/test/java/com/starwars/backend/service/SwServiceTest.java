package com.starwars.backend.service;

import com.starwars.backend.model.PeopleDto;
import com.starwars.backend.model.PageDto;
import com.starwars.backend.sorting.CreatedSort;
import com.starwars.backend.sorting.NameSort;
import com.starwars.backend.sorting.SortEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Unit tests for SwService, verifying filtering, sorting, and pagination behavior.
 */
@ExtendWith(MockitoExtension.class)
class SwServiceTest {

    @Mock
    private SwCacheService cacheService;

    private SwService service;

    @BeforeEach
    void setUp() {
        SortEngine sortEngine = new SortEngine(List.of(new NameSort(), new CreatedSort()));
        service = new SwService(cacheService, sortEngine);
    }

    @Test
    void filterByNameCaseInsensitiveAndPaginate() {
        // Given a PeopleDto matching "skY"
        PeopleDto luke = new PeopleDto();
        luke.setName("Luke Skywalker");

        // When cacheService is asked with "skY", return only Luke
        when(cacheService.loadAllPeople("skY"))
                .thenReturn(List.of(luke));

        // Call getPeople with search="skY", sort by name ascending
        PageDto<PeopleDto> resultPage = service.getPeople(
                0,      // page
                15,     // size
                "skY",  // search
                "name", // sort
                "asc"   // dir
        );

        // Then only one result and it's Luke Skywalker
        assertEquals(1, resultPage.getTotal(), "Should return exactly one result");
        assertEquals("Luke Skywalker",
                resultPage.getItems().get(0).getName(),
                "Filtered result should be Luke Skywalker");
    }

    @Test
    void sortByCreatedDescending() {
        // Given two PeopleDto with different creation timestamps
        PeopleDto older = new PeopleDto();
        older.setName("A");
        older.setCreated(OffsetDateTime.parse("2020-01-01T00:00:00Z"));

        PeopleDto newer = new PeopleDto();
        newer.setName("B");
        newer.setCreated(OffsetDateTime.parse("2021-01-01T00:00:00Z"));

        // When cacheService loads with no search, return [older, newer]
        when(cacheService.loadAllPeople(null))
                .thenReturn(List.of(older, newer));

        // Call getPeople sorted by 'created' descending
        PageDto<PeopleDto> resultPage = service.getPeople(
                0,         // page
                15,        // size
                null,      // search
                "created", // sort
                "desc"     // dir
        );

        // Then items should be [newer, older]
        assertEquals(List.of(newer, older),
                resultPage.getItems(),
                "Should sort by created date in descending order");
    }

    @Test
    void paginationReturnsEmptyListWhenPageOutOfRange() {
        // Given a list of 10 PeopleDto items named P0…P9
        List<PeopleDto> items = IntStream.range(0, 10)
                .mapToObj(i -> {
                    PeopleDto p = new PeopleDto();
                    p.setName("P" + i);
                    return p;
                })
                .toList();

        // When cacheService loads with no search, return the full list
        when(cacheService.loadAllPeople(null))
                .thenReturn(items);

        // Request page 5 with size 15 (out of range)
        PageDto<PeopleDto> resultPage = service.getPeople(
                5,      // page
                15,     // size
                null,   // search
                "name", // sort
                "asc"   // dir
        );

        // Then total stays 10 and items list is empty
        assertEquals(10,
                resultPage.getTotal(),
                "Total should remain the same as input list size");
        assertTrue(resultPage.getItems().isEmpty(),
                "Items should be empty when page is out of range");
    }
}
