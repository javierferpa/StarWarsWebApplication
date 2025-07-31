package com.starwars.backend.controller;

import com.starwars.backend.model.PageDto;
import com.starwars.backend.model.PeopleDto;
import com.starwars.backend.service.SwService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller slice tests for SwController, verifying:
 * 1) successful default retrieval of People,
 * 2) error handling mapped to JSON with correct status and message.
 */
@WebMvcTest(SwController.class)
class SwControllerMockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SwService swService;  // mock-injected service

    @Test
    void peopleDefaultReturnsPageDto() throws Exception {
        // Given a sample PeopleDto
        PeopleDto luke = new PeopleDto();
        luke.setName("Luke Skywalker");

        // And a PageDto<PeopleDto> containing that item
        PageDto<PeopleDto> page = PageDto.<PeopleDto>builder()
                .page(0)
                .size(15)
                .total(1)
                .items(List.of(luke))
                .build();

        // When the service is called with default parameters
        given(swService.getPeople(0, 15, null, "name", "asc"))
                .willReturn(page);

        // Then GET /api/people returns 200 with the expected JSON
        mockMvc.perform(get("/api/people"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(15))
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.items[0].name").value("Luke Skywalker"));
    }

    @Test
    void serviceErrorIsMappedToJsonErrorResponse() throws Exception {
        // Given the service throws a 502 Bad Gateway
        given(swService.getPeople(0, 15, null, "name", "asc"))
                .willThrow(new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Upstream down"));

        // When GET /api/people, then response is 502 with error JSON
        mockMvc.perform(get("/api/people"))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.status").value(502))
                .andExpect(jsonPath("$.message").value("Upstream down"));
    }
}
