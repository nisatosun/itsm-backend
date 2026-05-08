package com.nisa.itsm.sla.service;

import com.nisa.itsm.notification.service.NotificationService;
import com.nisa.itsm.sla.entity.SlaTracking;
import com.nisa.itsm.sla.repository.SlaTrackingRepository;
import com.nisa.itsm.ticket.entity.Ticket;
import com.nisa.itsm.user.entity.User;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SlaMonitoringServiceTest {

    @Test
    void shouldCreateRiskNotificationWhenTicketHasAssignee() {

        SlaTrackingRepository slaTrackingRepository =
                mock(SlaTrackingRepository.class);

        NotificationService notificationService =
                mock(NotificationService.class);

        SlaMonitoringService service =
                new SlaMonitoringService(
                        slaTrackingRepository,
                        notificationService
                );

        User assignee = new User();
        assignee.setId(2L);
        assignee.setUsername("agent_seed");

        Ticket ticket = new Ticket();
        ticket.setId(1L);
        ticket.setTicketNo("TCK-2026-000001");
        ticket.setAssignee(assignee);

        SlaTracking tracking = new SlaTracking();
        tracking.setTicket(ticket);

        when(slaTrackingRepository.findByBreachedFalseAndDueDateBetween(
                any(),
                any()
        )).thenReturn(List.of(tracking));

        service.checkSlaRisks();

        verify(notificationService, times(1))
                .createSlaRiskNotification(assignee, ticket);
    }

    @Test
    void shouldSkipRiskNotificationWhenTicketHasNoAssignee() {

        SlaTrackingRepository slaTrackingRepository =
                mock(SlaTrackingRepository.class);

        NotificationService notificationService =
                mock(NotificationService.class);

        SlaMonitoringService service =
                new SlaMonitoringService(
                        slaTrackingRepository,
                        notificationService
                );

        Ticket ticket = new Ticket();
        ticket.setId(1L);
        ticket.setTicketNo("TCK-2026-000001");
        ticket.setAssignee(null);

        SlaTracking tracking = new SlaTracking();
        tracking.setTicket(ticket);

        when(slaTrackingRepository.findByBreachedFalseAndDueDateBetween(
                any(),
                any()
        )).thenReturn(List.of(tracking));

        service.checkSlaRisks();

        verify(notificationService, never())
                .createSlaRiskNotification(any(), any());
    }
}