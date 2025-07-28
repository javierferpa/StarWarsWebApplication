package com.starwars.backend.sorting;

import com.starwars.backend.model.HasName;
import com.starwars.backend.model.PeopleDto;
import com.starwars.backend.model.PlanetDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for NameSort strategy, ensuring it supports the correct types
 * and performs case‑insensitive comparisons.
 */
@ExtendWith(MockitoExtension.class)
class NameSortTest {

    private final NameSort strategy = new NameSort();

    @Test
    void supportsPeopleAndPlanets() {
        // Should support sorting by name on both PeopleDto and PlanetDto
        assertTrue(strategy.supports(PeopleDto.class), "Should support PeopleDto");
        assertTrue(strategy.supports(PlanetDto.class), "Should support PlanetDto");
    }

    @Test
    void compareIsCaseInsensitive() {
        // Prepare two PeopleDto instances with different cases
        PeopleDto firstPerson = new PeopleDto();
        firstPerson.setName("alpha");

        PeopleDto secondPerson = new PeopleDto();
        secondPerson.setName("Beta");

        // Obtain a comparator for PeopleDto
        Comparator<HasName> comparator = strategy.comparator();

        // "alpha" should come before "Beta" in case-insensitive order
        assertTrue(comparator.compare(firstPerson, secondPerson) < 0,
                "Comparator should sort 'alpha' before 'Beta' (case‑insensitive)");
    }
}
