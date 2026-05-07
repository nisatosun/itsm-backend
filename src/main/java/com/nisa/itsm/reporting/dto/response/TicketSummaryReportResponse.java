package com.nisa.itsm.reporting.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketSummaryReportResponse {

    private long totalTickets;

    private long openTickets;

    private long inProgressTickets;

    private long resolvedTickets;

    private long closedTickets;

    private long highPriorityTickets;

    private long criticalPriorityTickets;
}
