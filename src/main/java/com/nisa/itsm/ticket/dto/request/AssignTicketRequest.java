package com.nisa.itsm.ticket.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Positive;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignTicketRequest {

    @NotNull(message = "Assignee ID is required")
    @Positive(message = "Assignee ID must be positive")
    private Long assigneeId;
}
