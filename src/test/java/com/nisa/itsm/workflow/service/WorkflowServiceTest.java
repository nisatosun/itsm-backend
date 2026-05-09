package com.nisa.itsm.workflow.service;

import com.nisa.itsm.audit.service.AuditLogService;
import com.nisa.itsm.common.enums.TicketStatus;
import com.nisa.itsm.exception.custom.BadRequestException;
import com.nisa.itsm.notification.service.NotificationService;
import com.nisa.itsm.ticket.entity.Ticket;
import com.nisa.itsm.ticket.repository.TicketRepository;
import com.nisa.itsm.user.entity.User;
import com.nisa.itsm.user.repository.UserRepository;
import com.nisa.itsm.workflow.dto.request.WorkflowTransitionRequest;
import com.nisa.itsm.workflow.entity.WorkflowHistory;
import com.nisa.itsm.workflow.repository.WorkflowHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkflowServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private WorkflowHistoryRepository workflowHistoryRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private AuditLogService auditLogService;

    @Mock
    private Authentication authentication;

    private WorkflowService workflowService;

    @BeforeEach
    void setUp() {
        workflowService = new WorkflowService(
                ticketRepository,
                userRepository,
                workflowHistoryRepository,
                notificationService,
                auditLogService);
    }

    @Test
    void transitionTicket_WhenResolvingWithResolutionNote_ShouldRecordTrimmedComment() {
        User user = user("agent");
        Ticket ticket = ticket(TicketStatus.IN_PROGRESS, user);
        WorkflowTransitionRequest request = request(TicketStatus.RESOLVED, "  Fixed by restart  ");

        when(authentication.getName()).thenReturn("agent");
        when(ticketRepository.findById(10L)).thenReturn(Optional.of(ticket));
        when(userRepository.findByUsername("agent")).thenReturn(Optional.of(user));
        when(workflowHistoryRepository.save(any(WorkflowHistory.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        workflowService.transitionTicket(10L, request, authentication);

        ArgumentCaptor<WorkflowHistory> historyCaptor = ArgumentCaptor.forClass(WorkflowHistory.class);
        verify(workflowHistoryRepository).save(historyCaptor.capture());

        WorkflowHistory savedHistory = historyCaptor.getValue();
        assertThat(ticket.getStatus()).isEqualTo(TicketStatus.RESOLVED);
        assertThat(savedHistory.getToStatus()).isEqualTo(TicketStatus.RESOLVED);
        assertThat(savedHistory.getComment()).isEqualTo("Fixed by restart");
    }

    @Test
    void transitionTicket_WhenResolvingWithoutResolutionNote_ShouldThrowBadRequest() {
        User user = user("agent");
        Ticket ticket = ticket(TicketStatus.IN_PROGRESS, user);
        WorkflowTransitionRequest request = request(TicketStatus.RESOLVED, null);

        when(authentication.getName()).thenReturn("agent");
        when(ticketRepository.findById(10L)).thenReturn(Optional.of(ticket));
        when(userRepository.findByUsername("agent")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> workflowService.transitionTicket(10L, request, authentication))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Resolution note is required when resolving a ticket");

        assertThat(ticket.getStatus()).isEqualTo(TicketStatus.IN_PROGRESS);
        verify(ticketRepository, never()).save(any(Ticket.class));
        verify(workflowHistoryRepository, never()).save(any(WorkflowHistory.class));
    }

    @Test
    void transitionTicket_WhenResolvingWithBlankResolutionNote_ShouldThrowBadRequest() {
        User user = user("agent");
        Ticket ticket = ticket(TicketStatus.IN_PROGRESS, user);
        WorkflowTransitionRequest request = request(TicketStatus.RESOLVED, "   ");

        when(authentication.getName()).thenReturn("agent");
        when(ticketRepository.findById(10L)).thenReturn(Optional.of(ticket));
        when(userRepository.findByUsername("agent")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> workflowService.transitionTicket(10L, request, authentication))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Resolution note is required when resolving a ticket");

        assertThat(ticket.getStatus()).isEqualTo(TicketStatus.IN_PROGRESS);
        verify(ticketRepository, never()).save(any(Ticket.class));
        verify(workflowHistoryRepository, never()).save(any(WorkflowHistory.class));
    }

    @Test
    void transitionTicket_WhenTargetIsNotResolved_ShouldAllowMissingComment() {
        User user = user("agent");
        Ticket ticket = ticket(TicketStatus.IN_PROGRESS, user);
        WorkflowTransitionRequest request = request(TicketStatus.WAITING_FOR_CUSTOMER, null);

        when(authentication.getName()).thenReturn("agent");
        when(ticketRepository.findById(10L)).thenReturn(Optional.of(ticket));
        when(userRepository.findByUsername("agent")).thenReturn(Optional.of(user));
        when(workflowHistoryRepository.save(any(WorkflowHistory.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        workflowService.transitionTicket(10L, request, authentication);

        ArgumentCaptor<WorkflowHistory> historyCaptor = ArgumentCaptor.forClass(WorkflowHistory.class);
        verify(workflowHistoryRepository).save(historyCaptor.capture());

        assertThat(ticket.getStatus()).isEqualTo(TicketStatus.WAITING_FOR_CUSTOMER);
        assertThat(historyCaptor.getValue().getComment()).isNull();
    }

    @Test
    void transitionTicket_WhenReopened_ShouldClearResolvedAtAndRecordReopenAction() {
        User user = user("agent");
        Ticket ticket = ticket(TicketStatus.RESOLVED, user);
        ticket.setResolvedAt(java.time.LocalDateTime.now());
        WorkflowTransitionRequest request = request(TicketStatus.IN_PROGRESS, "Reopened");

        when(authentication.getName()).thenReturn("agent");
        when(ticketRepository.findById(10L)).thenReturn(Optional.of(ticket));
        when(userRepository.findByUsername("agent")).thenReturn(Optional.of(user));
        when(workflowHistoryRepository.save(any(WorkflowHistory.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        workflowService.transitionTicket(10L, request, authentication);

        ArgumentCaptor<WorkflowHistory> historyCaptor = ArgumentCaptor.forClass(WorkflowHistory.class);
        verify(workflowHistoryRepository).save(historyCaptor.capture());

        assertThat(ticket.getStatus()).isEqualTo(TicketStatus.IN_PROGRESS);
        assertThat(ticket.getResolvedAt()).isNull();
        assertThat(historyCaptor.getValue().getAction()).isEqualTo("TICKET_REOPENED");
    }

    private static WorkflowTransitionRequest request(TicketStatus targetStatus, String comment) {
        WorkflowTransitionRequest request = new WorkflowTransitionRequest();
        request.setTargetStatus(targetStatus);
        request.setComment(comment);
        return request;
    }

    private static Ticket ticket(TicketStatus status, User requester) {
        Ticket ticket = new Ticket();
        ticket.setId(10L);
        ticket.setStatus(status);
        ticket.setRequester(requester);
        return ticket;
    }

    private static User user(String username) {
        User user = new User();
        user.setId(20L);
        user.setUsername(username);
        return user;
    }
}
