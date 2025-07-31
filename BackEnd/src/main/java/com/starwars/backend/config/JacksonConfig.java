package com.starwars.backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.OffsetDateTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Jackson configuration for custom date formatting.
 * Formats OffsetDateTime fields to "d-MM-yyyy HH:mm:ss" format in JSON responses
 * while preserving the internal OffsetDateTime type for proper sorting functionality.
 */
@Configuration
public class JacksonConfig {

    private static final DateTimeFormatter CUSTOM_DATE_FORMAT = 
            DateTimeFormatter.ofPattern("d-MM-yyyy HH:mm:ss");

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        
        // Register JavaTimeModule with custom OffsetDateTime serializer
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(OffsetDateTime.class, new CustomOffsetDateTimeSerializer());
        
        mapper.registerModule(javaTimeModule);
        
        return mapper;
    }

    /**
     * Custom serializer for OffsetDateTime that formats dates as "d-MM-yyyy HH:mm:ss"
     */
    private static class CustomOffsetDateTimeSerializer extends OffsetDateTimeSerializer {
        
        public CustomOffsetDateTimeSerializer() {
            super(OffsetDateTimeSerializer.INSTANCE, false, false, CUSTOM_DATE_FORMAT);
        }
    }
}
