package com.starwars.backend.sorting;

import com.starwars.backend.model.HasHeight;
import com.starwars.backend.model.PeopleDto;
import com.starwars.backend.model.PlanetDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for HeightSort, ensuring it only supports PeopleDto
 * and correctly parses height ("unknown" → 0).
 */
@ExtendWith(MockitoExtension.class)
class HeightSortTest {

    private final HeightSort strategy = new HeightSort();

    @Test
    void supportsOnlyPeople() {
        assertTrue(strategy.supports(PeopleDto.class),  "Should support PeopleDto");
        assertFalse(strategy.supports(PlanetDto.class), "Should not support PlanetDto");
    }

    @Test
    void unknownHeightBecomesZero() {
        PeopleDto a = new PeopleDto();
        a.setHeight("unknown");

        PeopleDto b = new PeopleDto();
        b.setHeight("50");

        Comparator<HasHeight> comp = strategy.comparator();
        assertTrue(comp.compare(a, b) < 0,
                "'unknown' should be treated as 0 and sorted before 50");
    }

    @Test
    void numericComparison() {
        PeopleDto shorter = new PeopleDto();
        shorter.setHeight("150");

        PeopleDto taller = new PeopleDto();
        taller.setHeight("200");

        Comparator<HasHeight> comp = strategy.comparator();
        assertTrue(comp.compare(shorter, taller) < 0,
                "150 cm should sort before 200 cm");
        assertTrue(comp.compare(taller, shorter) > 0,
                "200 cm should sort after 150 cm");
    }
}
