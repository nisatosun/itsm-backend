package com.nisa.itsm;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class InvalidTransitionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    // TODO: Add full invalid transition tests with real ticket database fixtures.
    // Example: CLOSED -> IN_PROGRESS should be rejected by workflow rules.
    // For now, this smoke test verifies that the status update endpoint rejects invalid request payloads.

    @Test
    @WithMockUser(authorities = "AGENT")
    void shouldRejectStatusUpdateWithInvalidPayload() throws Exception {
        mockMvc.perform(put("/api/tickets/99999/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}
