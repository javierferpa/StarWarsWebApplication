package com.starwars.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Central WebClient config for accessing the SWAPI backend.
 * I separate this as a config bean to avoid duplicating WebClient setup everywhere.
 * All services fetching data from swapi.info get this instance injected.
 */
@Configuration
public class WebClientConfig {

    @Bean
    public WebClient swapiWebClient(WebClient.Builder builder) {
        // Custom ExchangeStrategies: by default, WebClient limits response body to 256KB.
        // Some SWAPI endpoints can be quite big, so I set this to 16MB just to be safe.
        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                .codecs(configurer ->
                        configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024)
                )
                .build();

        // I always use JSON, so I set the Accept header globally here.
        // The base URL ensures all requests are relative ("/people", "/planets", etc).
        return builder
                .baseUrl("https://swapi.info/api")
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .exchangeStrategies(exchangeStrategies)
                .build();
    }
}
