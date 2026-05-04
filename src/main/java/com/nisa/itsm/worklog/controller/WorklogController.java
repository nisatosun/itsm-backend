package com.nisa.itsm.worklog.controller;

import com.nisa.itsm.worklog.dto.request.CreateWorklogRequest;
import com.nisa.itsm.worklog.dto.response.WorklogResponse;
import com.nisa.itsm.worklog.service.WorklogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/tickets/{id}/worklogs")
@RequiredArgsConstructor
public class WorklogController {

    private final WorklogService worklogService;

    @PostMapping
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
    public ResponseEntity<List<WorklogResponse>> getWorklogsByTicket(
            @PathVariable("id") Long ticketId,
            Principal principal
    ) {
        List<WorklogResponse> responses =
                worklogService.getWorklogsByTicket(ticketId, principal.getName());

        return ResponseEntity.ok(responses);
    }
}
