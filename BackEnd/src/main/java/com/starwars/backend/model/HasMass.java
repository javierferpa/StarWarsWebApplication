package com.starwars.backend.model;

/**
 * Interface for DTOs that have a mass field.
 * Used by the mass sorting strategy to handle numeric mass values.
 */
public interface HasMass {
    String getMass();
}
