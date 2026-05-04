package com.nisa.itsm.worklog.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class WorklogResponse {
    private Long id;
    private Long ticketId;
    private Long userId;
    private String username;
    private Integer minutesSpent;
    private String description;
    private LocalDateTime createdAt;
}
