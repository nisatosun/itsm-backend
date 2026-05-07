package com.nisa.itsm.reporting.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketTrendReportResponse {

    private long createdToday;

    private long createdLast7Days;

    private long createdLast30Days;
}
