package com.nisa.itsm.workflow.engine;

import com.nisa.itsm.common.enums.TicketStatus;
import com.nisa.itsm.exception.custom.BadRequestException;
import com.nisa.itsm.ticket.entity.Ticket;
import com.nisa.itsm.sla.service.SlaService;
import com.nisa.itsm.user.entity.User;
import com.nisa.itsm.workflow.service.WorkflowHistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class WorkflowEngineServiceImplTest {

    @Mock
    private WorkflowHistoryService workflowHistoryService;

    @Mock
    private SlaService slaService;

    private WorkflowEngineServiceImpl workflowEngineService;

    @BeforeEach
    void setUp() {
        workflowEngineService = new WorkflowEngineServiceImpl(workflowHistoryService, slaService);
    }

    @Test
    void executeAssignment_WhenTicketIsNew_ShouldTransitionToAssigned() {
        Ticket ticket = new Ticket();
        ticket.setId(1L);
        ticket.setStatus(TicketStatus.NEW);

        User assignee = new User();
        assignee.setId(10L);

        workflowEngineService.executeAssignment(ticket, assignee);

        assertThat(ticket.getAssignee()).isEqualTo(assignee);
        assertThat(ticket.getStatus()).isEqualTo(TicketStatus.ASSIGNED);

        verify(workflowHistoryService).recordTransition(
                eq(ticket),
                eq(TicketStatus.NEW),
                eq(TicketStatus.ASSIGNED),
                eq("STATUS_TRANSITION"),
                eq("Ticket moved to ASSIGNED"),
                eq(assignee));
    }

    @Test
    void executeAssignment_WhenTicketIsTriage_ShouldTransitionToAssigned() {
        Ticket ticket = new Ticket();
        ticket.setId(1L);
        ticket.setStatus(TicketStatus.TRIAGE);

        User assignee = new User();
        assignee.setId(10L);

        workflowEngineService.executeAssignment(ticket, assignee);

        assertThat(ticket.getAssignee()).isEqualTo(assignee);
        assertThat(ticket.getStatus()).isEqualTo(TicketStatus.ASSIGNED);

        verify(workflowHistoryService).recordTransition(
                eq(ticket),
                eq(TicketStatus.TRIAGE),
                eq(TicketStatus.ASSIGNED),
                eq("STATUS_TRANSITION"),
                eq("Ticket moved to ASSIGNED"),
                eq(assignee));
    }

    @Test
    void executeAssignment_WhenTicketIsNotNew_ShouldNotChangeStatus() {
        Ticket ticket = new Ticket();
        ticket.setId(2L);
        ticket.setStatus(TicketStatus.WAITING_FOR_CUSTOMER);

        User assignee = new User();
        assignee.setId(20L);

        workflowEngineService.executeAssignment(ticket, assignee);

        assertThat(ticket.getAssignee()).isEqualTo(assignee);
        assertThat(ticket.getStatus()).isEqualTo(TicketStatus.WAITING_FOR_CUSTOMER);
    }

    @Test
    void executeTransition_WhenTargetIsResolvedWithResolutionNote_ShouldSetResolvedAt() {
        Ticket ticket = new Ticket();
        ticket.setId(3L);
        ticket.setStatus(TicketStatus.IN_PROGRESS);

        workflowEngineService.executeTransition(ticket, TicketStatus.RESOLVED, null, "  Issue fixed  ");

        assertThat(ticket.getStatus()).isEqualTo(TicketStatus.RESOLVED);
        assertThat(ticket.getResolvedAt()).isNotNull();
        assertThat(ticket.getClosedAt()).isNull();

        verify(workflowHistoryService).recordTransition(
                eq(ticket),
                eq(TicketStatus.IN_PROGRESS),
                eq(TicketStatus.RESOLVED),
                eq("STATUS_TRANSITION"),
                eq("Issue fixed"),
                isNull());
    }

    @Test
    void executeTransition_WhenTargetIsResolvedWithoutResolutionNote_ShouldThrowBadRequest() {
        Ticket ticket = new Ticket();
        ticket.setId(6L);
        ticket.setStatus(TicketStatus.IN_PROGRESS);

        assertThatThrownBy(() -> workflowEngineService.executeTransition(ticket, TicketStatus.RESOLVED, null, null))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Resolution note is required when resolving a ticket");

        assertThat(ticket.getStatus()).isEqualTo(TicketStatus.IN_PROGRESS);
        verifyNoInteractions(slaService, workflowHistoryService);
    }

    @Test
    void executeTransition_WhenTargetIsResolvedWithBlankResolutionNote_ShouldThrowBadRequest() {
        Ticket ticket = new Ticket();
        ticket.setId(7L);
        ticket.setStatus(TicketStatus.IN_PROGRESS);

        assertThatThrownBy(() -> workflowEngineService.executeTransition(ticket, TicketStatus.RESOLVED, null, "   "))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Resolution note is required when resolving a ticket");

        assertThat(ticket.getStatus()).isEqualTo(TicketStatus.IN_PROGRESS);
        verifyNoInteractions(slaService, workflowHistoryService);
    }

    @Test
    void executeTransition_WhenTargetIsClosed_ShouldSetClosedAt() {
        Ticket ticket = new Ticket();
        ticket.setId(4L);
        ticket.setStatus(TicketStatus.RESOLVED);

        workflowEngineService.executeTransition(ticket, TicketStatus.CLOSED, null, null);

        assertThat(ticket.getStatus()).isEqualTo(TicketStatus.CLOSED);
        assertThat(ticket.getClosedAt()).isNotNull();

        verify(workflowHistoryService).recordTransition(
                eq(ticket),
                eq(TicketStatus.RESOLVED),
                eq(TicketStatus.CLOSED),
                eq("STATUS_TRANSITION"),
                isNull(),
                isNull());
    }

    @Test
    void executeTransition_WhenTargetIsInProgress_ShouldUpdateStatusWithoutDates() {
        Ticket ticket = new Ticket();
        ticket.setId(5L);
        ticket.setStatus(TicketStatus.NEW);

        workflowEngineService.executeTransition(ticket, TicketStatus.IN_PROGRESS, null, "Working on it");

        assertThat(ticket.getStatus()).isEqualTo(TicketStatus.IN_PROGRESS);
        assertThat(ticket.getResolvedAt()).isNull();
        assertThat(ticket.getClosedAt()).isNull();

        verify(workflowHistoryService).recordTransition(
                eq(ticket),
                eq(TicketStatus.NEW),
                eq(TicketStatus.IN_PROGRESS),
                eq("STATUS_TRANSITION"),
                eq("Working on it"),
                isNull());
    }

    @Test
    void executeTransition_WhenReopened_ShouldClearResolvedAtAndRecordReopenAction() {
        Ticket ticket = new Ticket();
        ticket.setId(8L);
        ticket.setStatus(TicketStatus.RESOLVED);
        ticket.setResolvedAt(java.time.LocalDateTime.now());

        workflowEngineService.executeTransition(ticket, TicketStatus.IN_PROGRESS, null, "Ticket reopened");

        assertThat(ticket.getStatus()).isEqualTo(TicketStatus.IN_PROGRESS);
        assertThat(ticket.getResolvedAt()).isNull();

        verify(workflowHistoryService).recordTransition(
                eq(ticket),
                eq(TicketStatus.RESOLVED),
                eq(TicketStatus.IN_PROGRESS),
                eq("TICKET_REOPENED"),
                eq("Ticket reopened"),
                isNull());
    }
}
