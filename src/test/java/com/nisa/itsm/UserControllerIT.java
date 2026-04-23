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
class UserControllerIT {

    @Autowired
    private MockMvc mockMvc;

    // ❌ Token yok → 403
    @Test
    void shouldReturnForbidden_whenNoToken() throws Exception {
        mockMvc.perform(get("/api/users")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isForbidden());
    }

    // ❌ USER role → 403
    @Test
    @WithMockUser(roles = "CUSTOMER")
    void shouldReturnForbidden_whenUserRole() throws Exception {
        mockMvc.perform(get("/api/users")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isForbidden());
    }

    // ✅ ADMIN role → 200
    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnOk_whenAdminRole() throws Exception {
        mockMvc.perform(get("/api/users")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk());
    }
}
