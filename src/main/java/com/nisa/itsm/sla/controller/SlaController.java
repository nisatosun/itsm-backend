package com.nisa.itsm.sla.controller;

import com.nisa.itsm.sla.dto.request.CreateSlaPolicyRequest;
import com.nisa.itsm.sla.dto.request.UpdateSlaPolicyRequest;
import com.nisa.itsm.sla.dto.response.SlaPolicyResponse;
import com.nisa.itsm.sla.dto.response.SlaTrackingResponse;
import com.nisa.itsm.sla.service.SlaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sla")
@RequiredArgsConstructor
public class SlaController {

    private final SlaService slaService;

    // ✅ GET policies
    @GetMapping("/policies")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MANAGER')")
    public List<SlaPolicyResponse> getPolicies() {
        return slaService.getPolicies();
    }

    // ✅ CREATE policy
    @PostMapping("/policies")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MANAGER')")
    public SlaPolicyResponse createPolicy(@Valid @RequestBody CreateSlaPolicyRequest request) {
        return slaService.createPolicy(request);
    }

    // ✅ UPDATE policy
    @PutMapping("/policies/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MANAGER')")
    public SlaPolicyResponse updatePolicy(
            @PathVariable Long id,
            @RequestBody UpdateSlaPolicyRequest request
    ) {
        return slaService.updatePolicy(id, request);
    }

    // ✅ GET ticket SLA
    @GetMapping("/tickets/{ticketId}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MANAGER') or hasAuthority('AGENT')")
    public SlaTrackingResponse getTicketSla(@PathVariable Long ticketId) {
        return slaService.getTicketSla(ticketId);
    }
}
