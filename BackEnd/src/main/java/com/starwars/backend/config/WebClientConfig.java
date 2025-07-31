package com.starwars.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Central WebClient configuration for accessing the SWAPI backend.
 * Provides a configured WebClient bean to avoid duplicating setup across services.
 * All services fetching data from swapi.info get this instance injected.
 */
@Configuration
public class WebClientConfig {

    @Bean
    public WebClient swapiWebClient(WebClient.Builder builder) {
        // Custom ExchangeStrategies: by default, WebClient limits response body to 256KB.
        // SWAPI endpoints can return large datasets, so setting to 16MB for safety.
        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                .codecs(configurer ->
                        configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024)
                )
                .build();

        // Configure JSON Accept header globally for all requests.
        // Base URL enables relative paths ("/people", "/planets", etc).
        return builder
                .baseUrl("https://swapi.info/api")
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .exchangeStrategies(exchangeStrategies)
                .build();
    }
}
