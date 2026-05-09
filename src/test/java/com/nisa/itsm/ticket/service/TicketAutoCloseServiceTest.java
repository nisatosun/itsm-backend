package com.nisa.itsm.ticket.service;

import com.nisa.itsm.audit.service.AuditLogService;
import com.nisa.itsm.common.enums.TicketStatus;
import com.nisa.itsm.ticket.entity.Ticket;
import com.nisa.itsm.ticket.repository.TicketRepository;
import com.nisa.itsm.user.entity.User;
import com.nisa.itsm.user.repository.UserRepository;
import com.nisa.itsm.workflow.engine.WorkflowEngineService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketAutoCloseServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private WorkflowEngineService workflowEngineService;

    @Mock
    private AuditLogService auditLogService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TicketAutoCloseService ticketAutoCloseService;

    private User systemUser;
    private Ticket oldTicket;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(ticketAutoCloseService, "autoCloseDays", 3);

        systemUser = new User();
        systemUser.setId(99L);
        systemUser.setUsername("system");

        oldTicket = new Ticket();
        oldTicket.setId(10L);
        oldTicket.setTicketNo("TCK-2026-000010");
        oldTicket.setStatus(TicketStatus.RESOLVED);
        oldTicket.setResolvedAt(LocalDateTime.now().minusDays(4));
    }

    @Test
    void autoCloseResolvedTickets_WhenOldTicketsExist_ShouldCloseThem() {
        when(ticketRepository.findByStatusAndResolvedAtBefore(eq(TicketStatus.RESOLVED), any(LocalDateTime.class)))
                .thenReturn(List.of(oldTicket));
        when(userRepository.findByUsername("system")).thenReturn(Optional.of(systemUser));

        ticketAutoCloseService.autoCloseResolvedTickets();

        verify(workflowEngineService).executeTransition(
                oldTicket,
                TicketStatus.CLOSED,
                systemUser,
                "Ticket automatically closed due to inactivity."
        );

        verify(auditLogService).logAction(
                "TICKET",
                10L,
                "AUTO_CLOSED",
                99L,
                "Ticket closed automatically",
                "RESOLVED",
                "CLOSED"
        );

        verify(ticketRepository).save(oldTicket);
    }

    @Test
    void autoCloseResolvedTickets_WhenNoOldTicketsExist_ShouldDoNothing() {
        when(ticketRepository.findByStatusAndResolvedAtBefore(eq(TicketStatus.RESOLVED), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        ticketAutoCloseService.autoCloseResolvedTickets();

        verifyNoInteractions(userRepository, workflowEngineService, auditLogService);
        verify(ticketRepository, never()).save(any());
    }
}
