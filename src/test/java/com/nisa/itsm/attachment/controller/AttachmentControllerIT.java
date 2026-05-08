package com.nisa.itsm.attachment.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AttachmentControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(authorities = "CUSTOMER", username = "customer1")
    void shouldRejectEmptyFileUpload() throws Exception {

        MockMultipartFile emptyFile =
                new MockMultipartFile(
                        "file",
                        "empty.txt",
                        "text/plain",
                        new byte[0]
                );

        mockMvc.perform(
                        multipart("/api/tickets/1/attachments")
                                .file(emptyFile)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "CUSTOMER", username = "customer1")
    void shouldRejectUnsupportedFileType() throws Exception {

        MockMultipartFile exeFile =
                new MockMultipartFile(
                        "file",
                        "virus.exe",
                        "application/octet-stream",
                        "fake exe content".getBytes()
                );

        mockMvc.perform(
                        multipart("/api/tickets/1/attachments")
                                .file(exeFile)
                )
                .andExpect(status().isBadRequest());
    }
}
