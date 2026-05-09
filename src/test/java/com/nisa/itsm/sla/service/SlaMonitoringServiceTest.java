package com.nisa.itsm.sla.service;

import com.nisa.itsm.audit.service.AuditLogService;
import com.nisa.itsm.common.enums.Role;
import com.nisa.itsm.common.enums.TicketStatus;
import com.nisa.itsm.notification.service.NotificationService;
import com.nisa.itsm.sla.entity.SlaTracking;
import com.nisa.itsm.sla.repository.SlaTrackingRepository;
import com.nisa.itsm.ticket.entity.Ticket;
import com.nisa.itsm.user.entity.User;
import com.nisa.itsm.user.repository.UserRepository;
import com.nisa.itsm.workflow.service.WorkflowHistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SlaMonitoringServiceTest {

    @Mock
    private SlaTrackingRepository slaTrackingRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private WorkflowHistoryService workflowHistoryService;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private SlaMonitoringService service;

    private User assignee;
    private Ticket ticket;
    private SlaTracking tracking;

    @BeforeEach
    void setUp() {
        assignee = new User();
        assignee.setId(2L);
        assignee.setUsername("agent_seed");

        ticket = new Ticket();
        ticket.setId(1L);
        ticket.setTicketNo("TCK-2026-000001");
        ticket.setStatus(TicketStatus.IN_PROGRESS);

        tracking = new SlaTracking();
        tracking.setTicket(ticket);
    }

    @Test
    void shouldCreateRiskNotificationWhenTicketHasAssignee() {
        ticket.setAssignee(assignee);

        when(slaTrackingRepository.findByBreachedFalseAndDueDateBetween(any(), any()))
                .thenReturn(List.of(tracking));

        service.checkSlaRisks();

        verify(notificationService, times(1)).createSlaRiskNotification(assignee, ticket);
    }

    @Test
    void shouldSkipRiskNotificationWhenTicketHasNoAssignee() {
        ticket.setAssignee(null);

        when(slaTrackingRepository.findByBreachedFalseAndDueDateBetween(any(), any()))
                .thenReturn(List.of(tracking));

        service.checkSlaRisks();

        verify(notificationService, never()).createSlaRiskNotification(any(), any());
    }

    @Test
    void shouldEscalateBreachedTicket() {
        ticket.setAssignee(assignee);

        User systemUser = new User();
        systemUser.setId(99L);
        systemUser.setUsername("system");

        User manager = new User();
        manager.setId(3L);

        when(slaTrackingRepository.findByBreachedFalseAndDueDateBefore(any(LocalDateTime.class)))
                .thenReturn(List.of(tracking));
        when(userRepository.findByUsername("system")).thenReturn(Optional.of(systemUser));
        when(userRepository.findByRolesIn(Set.of(Role.MANAGER, Role.ADMIN))).thenReturn(List.of(manager));

        service.checkSlaBreaches();

        assertTrue(tracking.getBreached());
        verify(workflowHistoryService).recordTransition(
                eq(ticket), eq(TicketStatus.IN_PROGRESS), eq(TicketStatus.IN_PROGRESS),
                eq("SLA_ESCALATED"), anyString(), eq(systemUser));
        verify(auditLogService).logAction(
                eq("TICKET"), eq(1L), eq("SLA_ESCALATED"), eq(99L), anyString(), eq("UNBREACHED"), eq("BREACHED"));
        verify(notificationService).createSlaBreachNotification(assignee, ticket);
        verify(notificationService).createSlaBreachNotification(manager, ticket);
        verify(slaTrackingRepository).save(tracking);
    }

    @Test
    void checkSlaBreaches_WhenNoBreaches_ShouldDoNothing() {
        when(slaTrackingRepository.findByBreachedFalseAndDueDateBefore(any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        service.checkSlaBreaches();

        verifyNoInteractions(userRepository, workflowHistoryService, auditLogService, notificationService);
        verify(slaTrackingRepository, never()).save(any());
    }
}