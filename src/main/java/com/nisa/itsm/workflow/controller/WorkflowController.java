package com.nisa.itsm.workflow.controller;

import com.nisa.itsm.workflow.dto.request.WorkflowTransitionRequest;
import com.nisa.itsm.workflow.dto.response.WorkflowHistoryResponse;
import com.nisa.itsm.workflow.service.WorkflowService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workflow")
@RequiredArgsConstructor
public class WorkflowController {

    private final WorkflowService workflowService;

    @PostMapping("/tickets/{ticketId}/transition")
    public WorkflowHistoryResponse transitionTicket(
            @PathVariable Long ticketId,
            @Valid @RequestBody WorkflowTransitionRequest request,
            Authentication authentication
    ) {
        return workflowService.transitionTicket(ticketId, request, authentication);
    }

    @GetMapping("/tickets/{ticketId}/history")
    public List<WorkflowHistoryResponse> getTicketWorkflowHistory(
            @PathVariable Long ticketId,
            Authentication authentication
    ) {
        return workflowService.getTicketWorkflowHistory(ticketId, authentication);
    }
}