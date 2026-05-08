package com.nisa.itsm.ticket.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TicketControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(authorities = "CUSTOMER")
    void shouldReturnBadRequest_whenTitleIsBlank() throws Exception {

        String requestBody = """
                {
                  "title": "",
                  "description": "Valid description text",
                  "categoryId": 1,
                  "priority": "HIGH"
                }
                """;

        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "CUSTOMER")
    void shouldReturnBadRequest_whenDescriptionTooShort() throws Exception {

        String requestBody = """
                {
                  "title": "Printer issue",
                  "description": "short",
                  "categoryId": 1,
                  "priority": "HIGH"
                }
                """;

        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "CUSTOMER")
    void shouldReturnBadRequest_whenPriorityIsNull() throws Exception {

        String requestBody = """
                {
                  "title": "Printer issue",
                  "description": "Valid description text",
                  "categoryId": 1,
                  "priority": null
                }
                """;

        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }
}
