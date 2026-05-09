package com.nisa.itsm.sla.service;

import com.nisa.itsm.notification.service.NotificationService;
import com.nisa.itsm.sla.entity.SlaTracking;
import com.nisa.itsm.sla.repository.SlaTrackingRepository;
import com.nisa.itsm.ticket.entity.Ticket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nisa.itsm.audit.service.AuditLogService;
import com.nisa.itsm.common.enums.Role;
import com.nisa.itsm.user.entity.User;
import com.nisa.itsm.user.repository.UserRepository;
import com.nisa.itsm.workflow.service.WorkflowHistoryService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class SlaMonitoringService {

    private final SlaTrackingRepository slaTrackingRepository;
    private final NotificationService notificationService;
    private final UserRepository userRepository;
    private final WorkflowHistoryService workflowHistoryService;
    private final AuditLogService auditLogService;

    @Scheduled(fixedRate = 300000)
    @Transactional
    public void checkSlaRisks() {

        LocalDateTime now = LocalDateTime.now();

        LocalDateTime warningLimit = now.plusHours(1);

        List<SlaTracking> riskyTrackings = slaTrackingRepository.findByBreachedFalseAndDueDateBetween(
                now,
                warningLimit);

        for (SlaTracking tracking : riskyTrackings) {

            Ticket ticket = tracking.getTicket();

            if (ticket.getAssignee() == null) {
                continue;
            }

            notificationService.createSlaRiskNotification(
                    ticket.getAssignee(),
                    ticket);

            log.info(
                    "SLA risk notification created for ticket {}",
                    ticket.getTicketNo());
        }
    }

    @Scheduled(fixedRate = 300000)
    @Transactional
    public void checkSlaBreaches() {
        LocalDateTime now = LocalDateTime.now();
        List<SlaTracking> breachedTrackings = slaTrackingRepository.findByBreachedFalseAndDueDateBefore(now);

        if (breachedTrackings.isEmpty()) {
            return;
        }

        User systemUser = userRepository.findByUsername("system")
                .orElseThrow(() -> new RuntimeException("System user not found for SLA escalation."));

        List<User> escalationManagers = userRepository.findByRolesIn(Set.of(Role.MANAGER, Role.ADMIN));

        for (SlaTracking tracking : breachedTrackings) {
            Ticket ticket = tracking.getTicket();

            tracking.setBreached(true);
            tracking.setBreachedAt(now);

            workflowHistoryService.recordTransition(
                    ticket,
                    ticket.getStatus(),
                    ticket.getStatus(),
                    "SLA_ESCALATED",
                    "Ticket SLA breached. Escalating to management.",
                    systemUser);

            auditLogService.logAction(
                    "TICKET",
                    ticket.getId(),
                    "SLA_ESCALATED",
                    systemUser.getId(),
                    "Ticket breached SLA",
                    "UNBREACHED",
                    "BREACHED");

            if (ticket.getAssignee() != null) {
                notificationService.createSlaBreachNotification(ticket.getAssignee(), ticket);
            }

            for (User manager : escalationManagers) {
                if (ticket.getAssignee() == null || !manager.getId().equals(ticket.getAssignee().getId())) {
                    notificationService.createSlaBreachNotification(manager, ticket);
                }
            }

            slaTrackingRepository.save(tracking);
            log.info("Ticket {} breached SLA and was escalated.", ticket.getTicketNo());
        }
    }
}