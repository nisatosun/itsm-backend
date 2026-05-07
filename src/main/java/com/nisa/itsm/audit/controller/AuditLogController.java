package com.nisa.itsm.audit.controller;

import com.nisa.itsm.audit.dto.response.AuditLogResponse;
import com.nisa.itsm.audit.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import java.util.List;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public List<AuditLogResponse> getAllLogs() {
        return auditLogService.getAllLogs();
    }

    @GetMapping(params = { "page", "size" })
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public Page<AuditLogResponse> getAllLogsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return auditLogService.getAllLogsPaged(page, size);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public AuditLogResponse getLogById(@PathVariable Long id) {
        return auditLogService.getLogById(id);
    }
}