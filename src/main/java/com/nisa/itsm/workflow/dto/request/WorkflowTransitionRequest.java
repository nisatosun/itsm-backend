package com.nisa.itsm.workflow.dto.request;

import com.nisa.itsm.common.enums.TicketStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkflowTransitionRequest {

    @NotNull
    private TicketStatus targetStatus;

    private String comment;
}