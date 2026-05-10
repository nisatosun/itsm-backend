package com.nisa.itsm.sla.controller;

import com.nisa.itsm.sla.dto.request.CreateSlaPolicyRequest;
import com.nisa.itsm.sla.dto.request.UpdateSlaPolicyRequest;
import com.nisa.itsm.sla.dto.response.SlaPolicyResponse;
import com.nisa.itsm.sla.dto.response.SlaTrackingResponse;
import com.nisa.itsm.sla.service.SlaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sla")
@RequiredArgsConstructor
@Tag(name = "SLA Controller", description = "Service Level Agreement policies and tracking endpoints")
public class SlaController {

    private final SlaService slaService;

    // ✅ GET policies
    @GetMapping("/policies")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MANAGER')")
    @Operation(summary = "Get all SLA policies", description = "Retrieves all configured SLA policies")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved SLA policies")
    public List<SlaPolicyResponse> getPolicies() {
        return slaService.getPolicies();
    }

    // ✅ CREATE policy
    @PostMapping("/policies")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MANAGER')")
    @Operation(summary = "Create SLA policy", description = "Creates a new SLA policy definition")
    @ApiResponse(responseCode = "200", description = "Successfully created SLA policy")
    public SlaPolicyResponse createPolicy(@Valid @RequestBody CreateSlaPolicyRequest request) {
        return slaService.createPolicy(request);
    }

    // ✅ UPDATE policy
    @PutMapping("/policies/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MANAGER')")
    @Operation(summary = "Update SLA policy", description = "Updates an existing SLA policy")
    @ApiResponse(responseCode = "200", description = "Successfully updated SLA policy")
    public SlaPolicyResponse updatePolicy(
            @PathVariable Long id,
            @RequestBody UpdateSlaPolicyRequest request
    ) {
        return slaService.updatePolicy(id, request);
    }

    // ✅ GET ticket SLA
    @GetMapping("/tickets/{ticketId}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MANAGER') or hasAuthority('AGENT')")
    @Operation(summary = "Get SLA tracking for ticket", description = "Retrieves current SLA status and deadlines for a ticket")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved ticket SLA tracking")
    public SlaTrackingResponse getTicketSla(@PathVariable Long ticketId) {
        return slaService.getTicketSla(ticketId);
    }
}
