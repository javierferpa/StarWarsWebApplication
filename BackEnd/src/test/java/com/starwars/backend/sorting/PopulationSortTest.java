package com.starwars.backend.sorting;

import com.starwars.backend.model.HasPopulation;
import com.starwars.backend.model.PeopleDto;
import com.starwars.backend.model.PlanetDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PopulationSort, ensuring it only supports PlanetDto
 * and treats null/"unknown" as zero.
 */
@ExtendWith(MockitoExtension.class)
class PopulationSortTest {

    private final PopulationSort strategy = new PopulationSort();

    @Test
    void supportsOnlyPlanets() {
        assertTrue(strategy.supports(PlanetDto.class),  "Should support PlanetDto");
        assertFalse(strategy.supports(PeopleDto.class), "Should not support PeopleDto");
    }

    @Test
    void nullOrUnknownPopulationBecomesZero() {
        PlanetDto a = new PlanetDto();
        a.setPopulation((String) null);

        PlanetDto b = new PlanetDto();
        b.setPopulation("1000");

        PlanetDto c = new PlanetDto();
        c.setPopulation("unknown");

        Comparator<HasPopulation> comp = strategy.comparator();

        // null -> 0
        assertTrue(comp.compare(a, b) < 0,
                "null should be treated as 0 and sorted before 1000");
        // "unknown" -> 0
        assertTrue(comp.compare(c, b) < 0,
                "'unknown' should be treated as 0 and sorted before 1000");
    }

    @Test
    void numericComparison() {
        PlanetDto small = new PlanetDto();
        small.setPopulation("500");

        PlanetDto big = new PlanetDto();
        big.setPopulation("2000");

        Comparator<HasPopulation> comp = strategy.comparator();

        assertTrue(comp.compare(small, big) < 0,
                "500 should be sorted before 2000");
        assertTrue(comp.compare(big, small) > 0,
                "2000 should be sorted after 500");
    }
}
