package com.nisa.itsm.sla.service;

import com.nisa.itsm.common.enums.TicketStatus;
import com.nisa.itsm.sla.entity.SlaTracking;
import com.nisa.itsm.sla.repository.SlaTrackingRepository;
import com.nisa.itsm.ticket.entity.Ticket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SlaServiceTest {

    @Mock
    private SlaTrackingRepository trackingRepository;

    @InjectMocks
    private SlaService slaService;

    private Ticket ticket;
    private SlaTracking tracking;

    @BeforeEach
    void setUp() {
        ticket = new Ticket();
        ticket.setId(1L);

        tracking = new SlaTracking();
        tracking.setTicket(ticket);
        tracking.setTotalPausedDurationMinutes(0L);
        tracking.setDueDate(LocalDateTime.now().plusHours(4));
        tracking.setFirstResponseDueDate(LocalDateTime.now().plusHours(1));
    }

    @Test
    void handleTicketTransition_WhenEnteringWaitingForCustomer_ShouldSetPausedAt() {
        when(trackingRepository.findByTicketId(1L)).thenReturn(Optional.of(tracking));

        slaService.handleTicketTransition(ticket, TicketStatus.IN_PROGRESS, TicketStatus.WAITING_FOR_CUSTOMER);

        assertThat(tracking.getPausedAt()).isNotNull();
    }

    @Test
    void handleTicketTransition_WhenLeavingWaitingForCustomer_ShouldResumeSla() {
        LocalDateTime pausedAt = LocalDateTime.now().minusMinutes(30);
        tracking.setPausedAt(pausedAt);
        LocalDateTime originalDueDate = tracking.getDueDate();
        LocalDateTime originalFirstResponseDueDate = tracking.getFirstResponseDueDate();

        when(trackingRepository.findByTicketId(1L)).thenReturn(Optional.of(tracking));

        slaService.handleTicketTransition(ticket, TicketStatus.WAITING_FOR_CUSTOMER, TicketStatus.IN_PROGRESS);

        assertThat(tracking.getPausedAt()).isNull();
        assertThat(tracking.getTotalPausedDurationMinutes()).isGreaterThanOrEqualTo(30L);
        assertThat(tracking.getDueDate()).isAfter(originalDueDate);
        assertThat(tracking.getFirstResponseDueDate()).isAfter(originalFirstResponseDueDate);
    }
}
