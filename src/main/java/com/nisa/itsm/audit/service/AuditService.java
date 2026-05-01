package com.nisa.itsm.audit.service;

import com.nisa.itsm.audit.entity.AuditLog;
import com.nisa.itsm.audit.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public void save(AuditLog auditLog) {
        auditLogRepository.save(auditLog);
    }
}