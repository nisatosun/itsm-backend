package com.nisa.itsm.audit.aspect;

import com.nisa.itsm.audit.annotation.Audit;
import com.nisa.itsm.audit.entity.AuditLog;
import com.nisa.itsm.audit.service.AuditService;
import com.nisa.itsm.user.entity.User;
import com.nisa.itsm.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditService auditService;
    private final UserRepository userRepository;

    @AfterReturning(value = "@annotation(audit)", argNames = "joinPoint,audit")
    public void logAudit(JoinPoint joinPoint, Audit audit) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Object[] args = joinPoint.getArgs();

        Long entityId = 0L;
        MultipartFile file = null;

        if (args.length > 0 && args[0] instanceof Long id) {
            entityId = id;
        }

        if (args.length > 1 && args[1] instanceof MultipartFile multipartFile) {
            file = multipartFile;
        }

        String description = file != null
                ? "Attachment uploaded: " + file.getOriginalFilename()
                : joinPoint.getSignature().toShortString();

        AuditLog auditLog = AuditLog.builder()
                .action(audit.action())
                .entityType("TICKET")
                .entityId(entityId)
                .performedBy(user.getId())
                .details(description)
                .createdAt(LocalDateTime.now())
                .build();

        auditService.save(auditLog);

        log.info("AUDIT SAVED: {}", audit.action());
    }
}
