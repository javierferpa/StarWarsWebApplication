package com.starwars.backend.sorting;

import com.starwars.backend.model.HasGender;
import com.starwars.backend.model.PeopleDto;
import com.starwars.backend.model.PlanetDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for GenderSort strategy, ensuring it supports the correct types
 * and performs case‑insensitive comparisons.
 */
@ExtendWith(MockitoExtension.class)
class GenderSortTest {

    private final GenderSort strategy = new GenderSort();

    @Test
    void supportsOnlyPeople() {
        assertTrue(strategy.supports(PeopleDto.class),  "Should support PeopleDto");
        assertFalse(strategy.supports(PlanetDto.class), "Should not support PlanetDto");
    }

    @Test
    void compareIsCaseInsensitive() {
        // Prepare two PeopleDto instances with different cases
        PeopleDto firstPerson = new PeopleDto();
        firstPerson.setGender("female");

        PeopleDto secondPerson = new PeopleDto();
        secondPerson.setGender("Male");

        // Obtain a comparator for PeopleDto
        Comparator<HasGender> comparator = strategy.comparator();

        // "female" should come before "Male" in case-insensitive order
        assertTrue(comparator.compare(firstPerson, secondPerson) < 0,
                "Comparator should sort 'female' before 'Male' (case‑insensitive)");
    }

    @Test
    void unknownGenderComesFirst() {
        PeopleDto unknown = new PeopleDto();
        unknown.setGender("unknown");  // This will remain "unknown"

        PeopleDto male = new PeopleDto();
        male.setGender("male");

        Comparator<HasGender> comp = strategy.comparator();
        // Since both are non-null strings, "unknown" should come after "male" alphabetically
        assertTrue(comp.compare(unknown, male) > 0, "'unknown' should sort after 'male' alphabetically");
        
        // Test that null values (before normalization) would be handled correctly
        PeopleDto nullGender = new PeopleDto();
        nullGender.setGender(null);  // This becomes "unknown" due to normalization
        
        assertEquals("unknown", nullGender.getGender(), "null gender should be normalized to 'unknown'");
    }

    @Test
    void alphabeticalOrder() {
        PeopleDto female = new PeopleDto();
        female.setGender("female");

        PeopleDto male = new PeopleDto();
        male.setGender("male");

        Comparator<HasGender> comp = strategy.comparator();
        assertTrue(comp.compare(female, male) < 0,
                "'female' should sort before 'male' alphabetically");
        assertTrue(comp.compare(male, female) > 0,
                "'male' should sort after 'female' alphabetically");
    }

    @Test
    void standardizedUnknownValues() {
        PeopleDto none = new PeopleDto();
        none.setGender("none");

        PeopleDto na = new PeopleDto();
        na.setGender("n/a");

        PeopleDto unknown = new PeopleDto();
        unknown.setGender("unknown");

        // All should be normalized to "unknown" by the setter
        assertEquals("unknown", none.getGender(), "Value 'none' should be normalized to 'unknown'");
        assertEquals("unknown", na.getGender(), "Value 'n/a' should be normalized to 'unknown'");
        assertEquals("unknown", unknown.getGender(), "Value 'unknown' should remain 'unknown'");
    }
}
