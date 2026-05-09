package com.nisa.itsm.ticket.service;

import com.nisa.itsm.audit.service.AuditLogService;
import com.nisa.itsm.common.enums.TicketStatus;
import com.nisa.itsm.ticket.entity.Ticket;
import com.nisa.itsm.ticket.repository.TicketRepository;
import com.nisa.itsm.user.entity.User;
import com.nisa.itsm.user.repository.UserRepository;
import com.nisa.itsm.workflow.engine.WorkflowEngineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketAutoCloseService {

    private final TicketRepository ticketRepository;
    private final WorkflowEngineService workflowEngineService;
    private final AuditLogService auditLogService;
    private final UserRepository userRepository;

    @Value("${itsm.ticket.auto-close-days:3}")
    private int autoCloseDays;

    @Scheduled(cron = "0 0 * * * *") // Runs every hour at minute 0
    @Transactional
    public void autoCloseResolvedTickets() {
        LocalDateTime deadline = LocalDateTime.now().minusDays(autoCloseDays);

        List<Ticket> pendingTickets = ticketRepository.findByStatusAndResolvedAtBefore(TicketStatus.RESOLVED, deadline);

        if (pendingTickets.isEmpty()) {
            return;
        }

        User systemUser = userRepository.findByUsername("system")
                .orElseThrow(() -> new RuntimeException("System user not found. Cannot auto-close tickets."));

        for (Ticket ticket : pendingTickets) {
            log.info("Auto-closing ticket {} (resolved at {})", ticket.getTicketNo(), ticket.getResolvedAt());

            workflowEngineService.executeTransition(
                    ticket,
                    TicketStatus.CLOSED,
                    systemUser,
                    "Ticket automatically closed due to inactivity."
            );

            auditLogService.logAction(
                    "TICKET",
                    ticket.getId(),
                    "AUTO_CLOSED",
                    systemUser.getId(),
                    "Ticket closed automatically",
                    TicketStatus.RESOLVED.name(),
                    TicketStatus.CLOSED.name()
            );

            ticketRepository.save(ticket);
        }
    }
}
