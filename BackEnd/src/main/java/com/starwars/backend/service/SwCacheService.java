package com.starwars.backend.service;

import com.starwars.backend.client.SwapiClient;
import com.starwars.backend.model.PeopleDto;
import com.starwars.backend.model.PlanetDto;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SwCacheService {
    private final SwapiClient swapiClient;

    @Cacheable(cacheNames = "peopleAll", key = "#search == null ? 'ALL' : #search.toLowerCase()")
    public List<PeopleDto> loadAllPeople(String search) {
        return swapiClient.fetchAllPeople(search)
                .blockOptional()
                .orElse(List.of());
    }

    @Cacheable(cacheNames = "planetsAll", key = "#search == null ? 'ALL' : #search.toLowerCase()")
    public List<PlanetDto> loadAllPlanets(String search) {
        return swapiClient.fetchAllPlanets(search)
                .blockOptional()
                .orElse(List.of());
    }
}
