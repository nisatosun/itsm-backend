package com.nisa.itsm.workflow.controller;

import com.nisa.itsm.workflow.dto.request.WorkflowTransitionRequest;
import com.nisa.itsm.workflow.dto.response.WorkflowHistoryResponse;
import com.nisa.itsm.workflow.service.WorkflowService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workflow")
@RequiredArgsConstructor
@Tag(name = "Workflow Controller", description = "jBPM workflow integration endpoints")
public class WorkflowController {

    private final WorkflowService workflowService;

    @PostMapping("/tickets/{ticketId}/transition")
    @Operation(summary = "Transition ticket workflow", description = "Executes a manual workflow state transition via jBPM")
    @ApiResponse(responseCode = "200", description = "Successfully transitioned workflow")
    public WorkflowHistoryResponse transitionTicket(
            @PathVariable Long ticketId,
            @Valid @RequestBody WorkflowTransitionRequest request,
            Authentication authentication
    ) {
        return workflowService.transitionTicket(ticketId, request, authentication);
    }

    @GetMapping("/tickets/{ticketId}/history")
    @Operation(summary = "Get workflow history (Legacy)", description = "Retrieves workflow history via the legacy workflow endpoint")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved workflow history")
    public List<WorkflowHistoryResponse> getTicketWorkflowHistory(
            @PathVariable Long ticketId,
            Authentication authentication
    ) {
        return workflowService.getTicketWorkflowHistory(ticketId, authentication);
    }
}