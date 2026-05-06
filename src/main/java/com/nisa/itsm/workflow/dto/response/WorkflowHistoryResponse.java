package com.nisa.itsm.workflow.dto.response;

import com.nisa.itsm.common.enums.TicketStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class WorkflowHistoryResponse {

    private Long id;
    private Long ticketId;
    private TicketStatus fromStatus;
    private TicketStatus toStatus;
    private String action;
    private String comment;
    private Long performedById;
    private String performedByUsername;
    private LocalDateTime createdAt;
}