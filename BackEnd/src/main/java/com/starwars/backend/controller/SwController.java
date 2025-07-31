package com.starwars.backend.controller;

import com.starwars.backend.model.PageDto;
import com.starwars.backend.model.PeopleDto;
import com.starwars.backend.model.PlanetDto;
import com.starwars.backend.service.SwService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SwController {

    private final SwService service;

    @GetMapping("/people")
    public PageDto<PeopleDto> people(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "name") String sort,
            @RequestParam(defaultValue = "asc") String dir
    ) {
        return service.getPeople(page, size, search, sort, dir);
    }

    @GetMapping("/planets")
    public PageDto<PlanetDto> planets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "name") String sort,
            @RequestParam(defaultValue = "asc") String dir
    ) {
        return service.getPlanets(page, size, search, sort, dir);
    }
}
