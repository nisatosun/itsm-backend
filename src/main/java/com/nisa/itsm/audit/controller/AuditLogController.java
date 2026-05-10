package com.nisa.itsm.audit.controller;

import com.nisa.itsm.audit.dto.response.AuditLogResponse;
import com.nisa.itsm.audit.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import java.util.List;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
@Tag(name = "Audit Log Controller", description = "System audit logs and compliance tracking")
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    @Operation(summary = "Get all audit logs", description = "Retrieves all system audit logs")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved audit logs")
    public List<AuditLogResponse> getAllLogs() {
        return auditLogService.getAllLogs();
    }

    @GetMapping(params = { "page", "size" })
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    @Operation(summary = "Get paginated audit logs", description = "Retrieves a paginated list of audit logs")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved paginated audit logs")
    public Page<AuditLogResponse> getAllLogsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return auditLogService.getAllLogsPaged(page, size);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    @Operation(summary = "Get audit log by ID", description = "Retrieves details of a specific audit log entry")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved audit log")
    public AuditLogResponse getLogById(@PathVariable Long id) {
        return auditLogService.getLogById(id);
    }
}