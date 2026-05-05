package com.nisa.itsm.sla.dto.response;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SlaTrackingResponse {

    private Long ticketId;
    private Long policyId;

    private LocalDateTime startTime;
    private LocalDateTime dueDate;
    private LocalDateTime firstResponseDueDate;

    private Boolean breached;
    private LocalDateTime breachedAt;

    private Long remainingMinutes;
    private String warningLevel;

    // getters setters
}