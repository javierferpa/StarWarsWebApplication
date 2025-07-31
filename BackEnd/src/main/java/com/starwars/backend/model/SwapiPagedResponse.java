package com.starwars.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SwapiPagedResponse<T> {
    private int count;
    private String next;
    private String previous;
    private List<T> results;
}
