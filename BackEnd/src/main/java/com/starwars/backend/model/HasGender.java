package com.starwars.backend.model;

/**
 * Interface for DTOs that have a gender field.
 * Used by the gender sorting strategy to handle alphabetical gender sorting.
 */
public interface HasGender {
    String getGender();
}
