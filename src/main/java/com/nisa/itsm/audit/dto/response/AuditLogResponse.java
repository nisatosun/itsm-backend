package com.nisa.itsm.audit.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AuditLogResponse {

    private Long id;
    private String entityType;
    private Long entityId;
    private String action;
    private Long performedBy;
    private String details;
    private String oldValue;
    private String newValue;
    private LocalDateTime createdAt;
}
