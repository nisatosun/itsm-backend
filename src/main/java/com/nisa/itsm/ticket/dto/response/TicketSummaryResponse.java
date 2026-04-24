package com.nisa.itsm.ticket.dto.response;

import com.nisa.itsm.common.enums.Priority;
import com.nisa.itsm.common.enums.TicketStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TicketSummaryResponse {

    private Long id;
    private String ticketNo;
    private String title;

    private Long categoryId;
    private String categoryName;

    private Priority priority;
    private TicketStatus status;

    private Long requesterId;
    private String requesterName;

    private Long assigneeId;
    private String assigneeName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
