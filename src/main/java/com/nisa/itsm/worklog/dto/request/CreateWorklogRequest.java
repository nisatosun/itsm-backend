package com.nisa.itsm.worklog.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateWorklogRequest {

    @NotNull
    @Min(1)
    private Integer minutesSpent;

    private String description;
}
