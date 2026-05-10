package com.nisa.itsm.worklog.controller;

import com.nisa.itsm.worklog.dto.request.CreateWorklogRequest;
import com.nisa.itsm.worklog.dto.response.WorklogResponse;
import com.nisa.itsm.worklog.service.WorklogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/tickets/{id}/worklogs")
@RequiredArgsConstructor
@Tag(name = "Worklog Controller", description = "Time tracking and effort logging for tickets")
public class WorklogController {

    private final WorklogService worklogService;

    @PostMapping
    @Operation(summary = "Create worklog", description = "Logs time spent (in minutes) on a specific ticket")
    @ApiResponse(responseCode = "201", description = "Worklog successfully created")
    public ResponseEntity<WorklogResponse> createWorklog(
            @PathVariable("id") Long ticketId,
            @Valid @RequestBody CreateWorklogRequest request,
            Principal principal
    ) {
        WorklogResponse response =
                worklogService.createWorklog(ticketId, request, principal.getName());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Get worklogs by ticket", description = "Retrieves all worklogs associated with a specific ticket")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved worklogs")
    public ResponseEntity<List<WorklogResponse>> getWorklogsByTicket(
            @PathVariable("id") Long ticketId,
            Principal principal
    ) {
        List<WorklogResponse> responses =
                worklogService.getWorklogsByTicket(ticketId, principal.getName());

        return ResponseEntity.ok(responses);
    }
}
