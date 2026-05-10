package com.nisa.itsm.workflow.controller;

import com.nisa.itsm.workflow.dto.response.WorkflowHistoryResponse;
import com.nisa.itsm.workflow.service.WorkflowHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tickets/{ticketId}/workflow-history")
@RequiredArgsConstructor
@Tag(name = "Workflow History Controller", description = "Ticket workflow history tracking")
public class WorkflowHistoryController {

    private final WorkflowHistoryService workflowHistoryService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','AGENT')")
    @Operation(summary = "Get workflow history", description = "Retrieves the complete state transition history for a ticket")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved workflow history")
    public ResponseEntity<List<WorkflowHistoryResponse>> getHistoryForTicket(@PathVariable Long ticketId) {
        List<WorkflowHistoryResponse> history = workflowHistoryService.getHistoryForTicket(ticketId);
        return ResponseEntity.ok(history);
    }
}
