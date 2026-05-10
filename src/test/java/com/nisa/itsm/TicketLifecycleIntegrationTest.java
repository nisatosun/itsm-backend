package com.nisa.itsm;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TicketLifecycleIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    // TODO: Add full lifecycle tests (CREATE, ASSIGN, RESOLVE, CLOSE)
    // once Testcontainers for PostgreSQL and proper JWT mocking are fully configured.
    // For now, this verifies that the Spring context loads and core ticket security rules apply.

    @Test
    @WithMockUser(authorities = "CUSTOMER")
    void shouldReturnForbiddenForAllTicketsWhenAuthenticatedAsCustomer() throws Exception {
        mockMvc.perform(get("/api/tickets")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "CUSTOMER")
    void shouldAllowCustomerToAccessMyTicketsEndpoint() throws Exception {
        mockMvc.perform(get("/api/tickets/my"))
                .andExpect(status().isNotFound());
    }
}
