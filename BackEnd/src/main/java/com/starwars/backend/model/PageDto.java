package com.starwars.backend.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PageDto<T> {
    private int page;
    private int size;
    private long total;
    private List<T> items;
}
