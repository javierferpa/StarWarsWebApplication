package com.starwars.backend.sorting;

import com.starwars.backend.model.HasMass;
import com.starwars.backend.model.PeopleDto;
import com.starwars.backend.model.PlanetDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MassSort, ensuring it only supports PeopleDto
 * and correctly parses mass ("unknown" → 0).
 */
@ExtendWith(MockitoExtension.class)
class MassSortTest {

    private final MassSort strategy = new MassSort();

    @Test
    void supportsOnlyPeople() {
        assertTrue(strategy.supports(PeopleDto.class),  "Should support PeopleDto");
        assertFalse(strategy.supports(PlanetDto.class), "Should not support PlanetDto");
    }

    @Test
    void unknownMassBecomesZero() {
        PeopleDto a = new PeopleDto();
        a.setMass("unknown");

        PeopleDto b = new PeopleDto();
        b.setMass("50");

        Comparator<HasMass> comp = strategy.comparator();
        assertTrue(comp.compare(a, b) < 0,
                "'unknown' should be treated as 0 and sorted before 50");
    }

    @Test
    void numericComparison() {
        PeopleDto lighter = new PeopleDto();
        lighter.setMass("65");

        PeopleDto heavier = new PeopleDto();
        heavier.setMass("89");

        Comparator<HasMass> comp = strategy.comparator();
        assertTrue(comp.compare(lighter, heavier) < 0,
                "65 kg should sort before 89 kg");
        assertTrue(comp.compare(heavier, lighter) > 0,
                "89 kg should sort after 65 kg");
    }

    @Test
    void decimalMassComparison() {
        PeopleDto a = new PeopleDto();
        a.setMass("75.5");

        PeopleDto b = new PeopleDto();
        b.setMass("75.8");

        Comparator<HasMass> comp = strategy.comparator();
        assertTrue(comp.compare(a, b) < 0,
                "75.5 should sort before 75.8");
    }

    @Test
    void massWithCommasHandling() {
        PeopleDto withComma = new PeopleDto();
        withComma.setMass("1,358");

        PeopleDto withoutComma = new PeopleDto();
        withoutComma.setMass("1000");

        Comparator<HasMass> comp = strategy.comparator();
        assertTrue(comp.compare(withoutComma, withComma) < 0,
                "1000 should sort before 1358 (parsed from '1,358')");
    }
}
