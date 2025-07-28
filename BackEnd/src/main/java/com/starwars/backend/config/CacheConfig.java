package com.starwars.backend.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Central cache configuration for the application.
 * - I use Caffeine because it's superfast and perfect for in-memory caching with Spring.
 * - By enabling @EnableCaching, Spring auto-wires the cache infrastructure and lets me
 *   use @Cacheable annotations throughout the codebase.
 */
@EnableCaching
@Configuration
public class CacheConfig {


    @Bean
    public Caffeine<Object, Object> caffeineConfig() {
        // Note: Caffeine's builder is immutable, so we create it once and share it below.
        return Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(10))
                .maximumSize(1000);
    }


    @Bean
    public CacheManager cacheManager(Caffeine<Object, Object> caffeine) {
        // CaffeineCacheManager is a Spring abstraction that hides most of the boilerplate.
        CaffeineCacheManager manager = new CaffeineCacheManager("peopleAll", "planetsAll");
        manager.setCaffeine(caffeine);
        // No explicit logging here—Spring Boot logs cache creation on startup anyway.
        return manager;
    }
}
