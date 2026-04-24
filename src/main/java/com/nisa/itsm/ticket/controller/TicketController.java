package com.nisa.itsm.ticket.controller;

import com.nisa.itsm.ticket.dto.request.CreateTicketRequest;
import com.nisa.itsm.ticket.dto.response.TicketDetailResponse;
import com.nisa.itsm.ticket.dto.response.TicketSummaryResponse;
import com.nisa.itsm.ticket.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PostMapping
    public ResponseEntity<TicketDetailResponse> createTicket(
            @Valid @RequestBody CreateTicketRequest request,
            Principal principal) {
        return ResponseEntity.ok(ticketService.createTicket(request, principal.getName()));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'AGENT', 'MANAGER')")
    public ResponseEntity<List<TicketSummaryResponse>> getAllTickets() {
        return ResponseEntity.ok(ticketService.getAllTickets());
    }

    @GetMapping("/my")
    public ResponseEntity<List<TicketSummaryResponse>> getMyTickets(Principal principal) {
        return ResponseEntity.ok(ticketService.getMyTickets(principal.getName()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketDetailResponse> getTicketById(
            @PathVariable Long id, 
            Principal principal, 
            Authentication authentication) {
        
        boolean isPrivileged = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN") || 
                               a.getAuthority().equals("AGENT") || 
                               a.getAuthority().equals("MANAGER"));

        return ResponseEntity.ok(ticketService.getTicketByIdAndCheckAccess(id, principal.getName(), isPrivileged));
    }
}
