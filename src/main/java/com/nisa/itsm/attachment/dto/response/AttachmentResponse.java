package com.nisa.itsm.attachment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentResponse {
    private Long id;
    private Long ticketId;
    private Long uploadedById;
    private String uploadedByUsername;
    private String originalFilename;
    private String contentType;
    private Long fileSize;
    private LocalDateTime createdAt;
}
