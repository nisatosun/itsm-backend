package com.nisa.itsm.reporting.controller;

import com.nisa.itsm.reporting.dto.response.AgentPerformanceReportResponse;
import com.nisa.itsm.reporting.dto.response.SlaComplianceReportResponse;
import com.nisa.itsm.reporting.dto.response.TicketSummaryReportResponse;
import com.nisa.itsm.reporting.dto.response.TicketTrendReportResponse;
import com.nisa.itsm.reporting.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
    @Operation(summary = "Get ticket summary report", description = "Retrieves an aggregated snapshot of all ticket statuses and priorities")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved summary metrics")
    public TicketSummaryReportResponse getTicketSummary() {
        return reportService.getTicketSummary();
    }

    @GetMapping("/sla/compliance")
    @Operation(summary = "Get SLA compliance report", description = "Retrieves overall SLA compliance rate, optionally filtered by days")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved SLA metrics")
    public SlaComplianceReportResponse getSlaCompliance(
            @RequestParam(defaultValue = "30", required = false) Integer days) {
        return reportService.getSlaCompliance(days);
    }

    @GetMapping("/tickets/trends")
    @Operation(summary = "Get ticket trend report", description = "Retrieves ticket creation volume. The requested rolling days window (default 7) maps to the 'createdLast7Days' DTO field.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved ticket trends")
    public TicketTrendReportResponse getTicketTrends(
            @RequestParam(defaultValue = "7", required = false) Integer days) {
        return reportService.getTicketTrends(days);
    }

    @GetMapping("/agents/performance")
    @Operation(summary = "Get agent performance report", description = "Retrieves ticket and worklog aggregates for all active agents")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved agent performance metrics")
    public List<AgentPerformanceReportResponse> getAgentPerformance() {
        return reportService.getAgentPerformance();
    }
}
