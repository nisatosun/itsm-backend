package com.nisa.itsm.reporting.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentPerformanceReportResponse {

    private Long agentId;

    private String agentUsername;

    private long assignedTicketCount;

    private long resolvedTicketCount;

    private int worklogMinutes;
}
