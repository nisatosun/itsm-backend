package com.nisa.itsm.audit.service;

import com.nisa.itsm.audit.dto.response.AuditLogResponse;
import com.nisa.itsm.audit.entity.AuditLog;
import com.nisa.itsm.audit.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public List<AuditLogResponse> getAllLogs() {

        return auditLogRepository.findAll(
                        Sort.by(Sort.Direction.DESC, "createdAt")
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public AuditLogResponse getLogById(Long id) {

        AuditLog auditLog = auditLogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Audit log not found"));
        return toResponse(auditLog);
    }

    @Transactional
    public void logAction(
            String entityType,
            Long entityId,
            String action,
            Long performedBy,
            String details,
            String oldValue,
            String newValue
    ) {
        AuditLog auditLog = AuditLog.builder()
                .entityType(entityType)
                .entityId(entityId)
                .action(action)
                .performedBy(performedBy)
                .details(details)
                .oldValue(oldValue)
                .newValue(newValue)
                .createdAt(java.time.LocalDateTime.now())
                .build();

        auditLogRepository.save(auditLog);
    }

    private AuditLogResponse toResponse(AuditLog auditLog) {

        return AuditLogResponse.builder()
                .id(auditLog.getId())
                .entityType(auditLog.getEntityType())
                .entityId(auditLog.getEntityId())
                .action(auditLog.getAction())
                .performedBy(auditLog.getPerformedBy())
                .details(auditLog.getDetails())
                .oldValue(auditLog.getOldValue())
                .newValue(auditLog.getNewValue())
                .createdAt(auditLog.getCreatedAt())
                .build();
    }
}