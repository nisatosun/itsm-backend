package com.nisa.itsm.ticket.dto.request;

import com.nisa.itsm.common.enums.TicketStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTicketStatusRequest {
    
    @NotNull(message = "Status is required")
    private TicketStatus status;
}
