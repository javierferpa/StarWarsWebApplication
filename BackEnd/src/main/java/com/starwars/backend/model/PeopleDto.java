package com.starwars.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PeopleDto implements HasName, HasCreated, HasHeight, HasMass, HasGender {
    private String name;
    private String height;
    private String mass;

    @JsonProperty("hair_color")
    private String hairColor;

    @JsonProperty("skin_color")
    private String skinColor;

    @JsonProperty("eye_color")
    private String eyeColor;

    @JsonProperty("birth_year")
    private String birthYear;

    private String gender;

    private String homeworld;

    private List<String> films;
    private List<String> species;
    private List<String> vehicles;
    private List<String> starships;

    private OffsetDateTime created;
    private OffsetDateTime edited;
    private String url;

    /**
     * Custom setter for mass to handle comma-separated thousands and invalid values.
     * Removes commas from values like "1,358" and normalizes to "1358".
     * Invalid or unknown values become "unknown".
     */
    @JsonProperty("mass")
    public void setMass(String raw) {
        this.mass = normalizeMass(raw);
    }

    /**
     * Custom setter for gender to standardize inconsistent values.
     * Normalizes "none", "n/a", empty strings to "unknown".
     */
    @JsonProperty("gender")
    public void setGender(String raw) {
        this.gender = normalizeGender(raw);
    }

    private static String normalizeMass(String raw) {
        if (raw == null || raw.isBlank()) return "unknown";
        String v = raw.trim();
        if ("unknown".equalsIgnoreCase(v) || "n/a".equalsIgnoreCase(v)) return "unknown";
        
        // Remove commas from thousands separator (e.g., "1,358" -> "1358")
        v = v.replace(",", "");
        
        // Validate it's a valid number
        try {
            Double.parseDouble(v);
            return v; // Return the cleaned numeric value
        } catch (NumberFormatException e) {
            return "unknown";
        }
    }

    private static String normalizeGender(String raw) {
        if (raw == null || raw.isBlank()) return "unknown";
        String v = raw.trim().toLowerCase();
        
        // Standardize various "unknown" representations
        if ("none".equals(v) || "n/a".equals(v) || "unknown".equals(v) || "null".equals(v)) {
            return "unknown";
        }
        
        // Return standardized gender values
        return v; // Keep the original value (male, female, hermaphrodite, etc.)
    }
}
