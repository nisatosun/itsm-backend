package com.nisa.itsm.reporting.controller;

import com.nisa.itsm.reporting.dto.response.AgentPerformanceReportResponse;
import com.nisa.itsm.reporting.dto.response.SlaComplianceReportResponse;
import com.nisa.itsm.reporting.dto.response.TicketSummaryReportResponse;
import com.nisa.itsm.reporting.dto.response.TicketTrendReportResponse;
import com.nisa.itsm.reporting.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "Reports", description = "Reporting and dashboard endpoints")
@PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/tickets/summary")
    @Operation(summary = "Get ticket summary report")
    public TicketSummaryReportResponse getTicketSummary() {
        return reportService.getTicketSummary();
    }

    @GetMapping("/sla/compliance")
    @Operation(summary = "Get SLA compliance report")
    public SlaComplianceReportResponse getSlaCompliance() {
        return reportService.getSlaCompliance();
    }

    @GetMapping("/tickets/trends")
    @Operation(summary = "Get ticket trend report")
    public TicketTrendReportResponse getTicketTrends() {
        return reportService.getTicketTrends();
    }

    @GetMapping("/agents/performance")
    @Operation(summary = "Get agent performance report")
    public List<AgentPerformanceReportResponse> getAgentPerformance() {
        return reportService.getAgentPerformance();
    }
}
