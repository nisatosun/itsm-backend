package com.nisa.itsm.ticket.dto.response;

import com.nisa.itsm.common.enums.Priority;
import com.nisa.itsm.common.enums.TicketStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TicketDetailResponse {

    private Long id;
    private String ticketNo;
    private String title;
    private String description;

    private Long categoryId;
    private String categoryName;

    private Priority priority;
    private TicketStatus status;

    private Long requesterId;
    private String requesterName;
    private String requesterEmail;

    private Long assigneeId;
    private String assigneeName;
    private String assigneeEmail;

    private Long processInstanceId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;
    private LocalDateTime closedAt;
}
