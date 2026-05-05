package com.nisa.itsm.sla.dto.response;

import com.nisa.itsm.common.enums.Priority;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SlaPolicyResponse {

    private Long id;
    private Priority priority;
    private Integer responseTimeHours;
    private Integer resolutionTimeHours;
    private Boolean active;

    // getters setters
}
