package com.nisa.itsm.sla.dto.request;

import com.nisa.itsm.common.enums.Priority;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateSlaPolicyRequest {

    @NotNull
    private Priority priority;

    @NotNull
    private Integer responseTimeHours;

    @NotNull
    private Integer resolutionTimeHours;

    private Boolean active;

    // getters setters
}