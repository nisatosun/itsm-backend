package com.nisa.itsm.ticket.dto.request;

import com.nisa.itsm.common.enums.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTicketRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 150, message = "Title must be at most 150 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(
            min = 10,
            max = 5000,
            message = "Description must be between 10 and 5000 characters"
    )
    private String description;

    @NotNull(message = "Category is required")
    private Long categoryId;

    @NotNull(message = "Priority is required")
    private Priority priority;
}
