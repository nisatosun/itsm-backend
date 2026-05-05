package com.nisa.itsm.sla.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateSlaPolicyRequest {

    private Integer responseTimeHours;
    private Integer resolutionTimeHours;
    private Boolean active;

    // getters setters
}
