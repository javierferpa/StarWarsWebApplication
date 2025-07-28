package com.starwars.backend.client;

import com.starwars.backend.model.PeopleDto;
import com.starwars.backend.model.PlanetDto;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SwapiClientTest {

    private static final Duration BLOCK_TIMEOUT = Duration.ofSeconds(5);

    private MockWebServer server;
    private SwapiClient client;

    @BeforeEach
    void setUp() throws IOException {
        server = new MockWebServer();
        server.start();

        // I set the baseUrl to end with /api so relative paths like /people/ resolve properly.
        WebClient webClient = WebClient.builder()
                .baseUrl(server.url("/api").toString())
                .build();

        client = new SwapiClient(webClient);
    }

    @AfterEach
    void tearDown() throws IOException {
        server.shutdown();
    }

    @Test
    void fetchAllPeople_followsNextLink() throws InterruptedException {
        // Two paged responses; the first includes the next link, the second ends the chain.
        String page1 = """
            {"count":2,"next":"/people/?page=2","previous":null,"results":[{"name":"A"}]}
            """;
        String page2 = """
            {"count":2,"next":null,"previous":"/people/?page=1","results":[{"name":"B"}]}
            """;

        server.enqueue(json(page1));
        server.enqueue(json(page2));

        List<PeopleDto> all = client.fetchAllPeople(null).block(BLOCK_TIMEOUT);

        assertNotNull(all, "Result list should not be null");
        assertEquals(2, all.size(), "Should fetch two entries");
        assertEquals("A", all.get(0).getName(), "First entry should be 'A'");
        assertEquals("B", all.get(1).getName(), "Second entry should be 'B'");

        // I also assert the requested paths to ensure we follow the next link correctly.
        RecordedRequest r1 = server.takeRequest();
        assertEquals("/api/people/?page=1", r1.getPath());

        RecordedRequest r2 = server.takeRequest();
        assertEquals("/api/people/?page=2", r2.getPath());
    }

    @Test
    void fetchAllPeople_fallbackToPlainArray() throws InterruptedException {
        // First response is a flat array (will fail when decoding as paged).
        // The client should then call the fallback endpoint and parse the array.
        String flatArray = """
            [{"name":"Luke Skywalker"},{"name":"Darth Vader"}]
            """;

        server.enqueue(json(flatArray)); // attempt to read paged -> fails -> triggers fallback
        server.enqueue(json(flatArray)); // fallback GET /people/

        List<PeopleDto> all = client.fetchAllPeople(null).block(BLOCK_TIMEOUT);

        assertNotNull(all, "Result list should not be null");
        assertEquals(2, all.size(), "Should parse two entries from flat array");
        assertEquals("Luke Skywalker", all.get(0).getName(), "First entry should be 'Luke Skywalker'");
        assertEquals("Darth Vader", all.get(1).getName(), "Second entry should be 'Darth Vader'");

        // First request tries the paged endpoint (page=1)…
        RecordedRequest r1 = server.takeRequest();
        assertTrue(r1.getPath().startsWith("/api/people/"), "First request should target /api/people/");
        // …then the fallback hits /people/ (no query params required)
        RecordedRequest r2 = server.takeRequest();
        assertEquals("/api/people/", r2.getPath());
    }

    @Test
    void fetchAllPlanets_followsNextLink() throws InterruptedException {
        String page1 = """
            {"count":2,"next":"/planets/?page=2","previous":null,"results":[{"name":"X"}]}
            """;
        String page2 = """
            {"count":2,"next":null,"previous":"/planets/?page=1","results":[{"name":"Y"}]}
            """;

        server.enqueue(json(page1));
        server.enqueue(json(page2));

        List<PlanetDto> all = client.fetchAllPlanets(null).block(BLOCK_TIMEOUT);

        assertNotNull(all, "Result list should not be null");
        assertEquals(2, all.size(), "Should fetch two entries");
        assertEquals("X", all.get(0).getName(), "First entry should be 'X'");
        assertEquals("Y", all.get(1).getName(), "Second entry should be 'Y'");

        RecordedRequest r1 = server.takeRequest();
        assertEquals("/api/planets/?page=1", r1.getPath());

        RecordedRequest r2 = server.takeRequest();
        assertEquals("/api/planets/?page=2", r2.getPath());
    }

    @Test
    void fetchAllPlanets_fallbackToPlainArray() throws InterruptedException {
        String flatArray = """
            [{"name":"Tatooine"},{"name":"Alderaan"}]
            """;

        server.enqueue(json(flatArray)); // attempt to read paged -> fails
        server.enqueue(json(flatArray)); // fallback GET /planets/

        List<PlanetDto> all = client.fetchAllPlanets(null).block(BLOCK_TIMEOUT);

        assertNotNull(all, "Result list should not be null");
        assertEquals(2, all.size(), "Should parse two entries from flat array");
        assertEquals("Tatooine", all.get(0).getName(), "First entry should be 'Tatooine'");
        assertEquals("Alderaan", all.get(1).getName(), "Second entry should be 'Alderaan'");

        RecordedRequest r1 = server.takeRequest();
        assertTrue(r1.getPath().startsWith("/api/planets/"), "First request should target /api/planets/");
        RecordedRequest r2 = server.takeRequest();
        assertEquals("/api/planets/", r2.getPath());
    }

    // ---- helpers ----

    private static MockResponse json(String body) {
        return new MockResponse()
                .setBody(body)
                .addHeader("Content-Type", "application/json");
    }
}
