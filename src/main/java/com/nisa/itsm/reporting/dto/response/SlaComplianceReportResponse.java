package com.nisa.itsm.reporting.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SlaComplianceReportResponse {

    private long totalTrackedTickets;

    private long breachedTickets;

    private long nonBreachedTickets;

    private double complianceRate;
}
