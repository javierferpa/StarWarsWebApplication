package com.starwars.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlanetDto implements HasName, HasCreated, HasPopulation {
    private String name;

    @JsonProperty("rotation_period")
    private String rotationPeriod;

    @JsonProperty("orbital_period")
    private String orbitalPeriod;

    private String diameter;
    private String climate;
    private String gravity;
    private String terrain;

    @JsonProperty("surface_water")
    private String surfaceWater;

    @Setter(AccessLevel.NONE)
    private Long population;

    private List<String> residents;
    private List<String> films;

    private OffsetDateTime created;
    private OffsetDateTime edited;
    private String url;

    @JsonProperty("population")
    public void setPopulation(String raw) {
        this.population = parsePopulation(raw);
    }

    private static Long parsePopulation(String raw) {
        if (raw == null || raw.isBlank()) return null;
        String v = raw.trim();
        if ("unknown".equalsIgnoreCase(v) || "n/a".equalsIgnoreCase(v)) return null;
        v = v.replace(",", "");
        try {
            return Long.parseLong(v);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
