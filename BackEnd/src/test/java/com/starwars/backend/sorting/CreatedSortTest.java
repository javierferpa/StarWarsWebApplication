package com.starwars.backend.sorting;

import com.starwars.backend.model.HasCreated;
import com.starwars.backend.model.PeopleDto;
import com.starwars.backend.model.PlanetDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CreatedSort, ensuring it supports the correct DTOs
 * and sorts by creation date properly (null first).
 */
@ExtendWith(MockitoExtension.class)
class CreatedSortTest {

    private final CreatedSort strategy = new CreatedSort();

    @Test
    void supportsPeopleAndPlanets() {
        assertTrue(strategy.supports(PeopleDto.class), "Should support PeopleDto");
        assertTrue(strategy.supports(PlanetDto.class), "Should support PlanetDto");
    }

    @Test
    void nullCreatedComesFirst() {
        PeopleDto a = new PeopleDto();
        a.setCreated(null);

        PeopleDto b = new PeopleDto();
        b.setCreated(OffsetDateTime.now(ZoneOffset.UTC));

        Comparator<HasCreated> comp = strategy.comparator();
        assertTrue(comp.compare(a, b) < 0, "null should sort before a non-null date");
    }

    @Test
    void olderDateBeforeNewer() {
        OffsetDateTime older = OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime newer = OffsetDateTime.of(2021, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);

        PeopleDto p1 = new PeopleDto();
        p1.setCreated(older);
        PeopleDto p2 = new PeopleDto();
        p2.setCreated(newer);

        Comparator<HasCreated> comp = strategy.comparator();
        assertTrue(comp.compare(p1, p2) < 0, "older date should sort before newer date");
        assertTrue(comp.compare(p2, p1) > 0, "newer date should sort after older date");
    }
}
