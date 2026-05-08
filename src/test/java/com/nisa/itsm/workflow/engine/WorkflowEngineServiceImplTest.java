package com.nisa.itsm.workflow.engine;

import com.nisa.itsm.common.enums.TicketStatus;
import com.nisa.itsm.ticket.entity.Ticket;
import com.nisa.itsm.user.entity.User;
import com.nisa.itsm.workflow.service.WorkflowHistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class WorkflowEngineServiceImplTest {

    @Mock
    private WorkflowHistoryService workflowHistoryService;

    private WorkflowEngineServiceImpl workflowEngineService;

    @BeforeEach
    void setUp() {
        workflowEngineService = new WorkflowEngineServiceImpl(workflowHistoryService);
    }

    @Test
    void executeAssignment_WhenTicketIsNew_ShouldTransitionToInProgress() {
        Ticket ticket = new Ticket();
        ticket.setId(1L);
        ticket.setStatus(TicketStatus.NEW);

        User assignee = new User();
        assignee.setId(10L);

        workflowEngineService.executeAssignment(ticket, assignee);

        assertThat(ticket.getAssignee()).isEqualTo(assignee);
        assertThat(ticket.getStatus()).isEqualTo(TicketStatus.IN_PROGRESS);
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
    void executeTransition_WhenTargetIsResolved_ShouldSetResolvedAt() {
        Ticket ticket = new Ticket();
        ticket.setId(3L);
        ticket.setStatus(TicketStatus.IN_PROGRESS);

        workflowEngineService.executeTransition(ticket, TicketStatus.RESOLVED, null, null);

        assertThat(ticket.getStatus()).isEqualTo(TicketStatus.RESOLVED);
        assertThat(ticket.getResolvedAt()).isNotNull();
        assertThat(ticket.getClosedAt()).isNull();

        verify(workflowHistoryService).recordTransition(
                eq(ticket),
                eq(TicketStatus.IN_PROGRESS),
                eq(TicketStatus.RESOLVED),
                eq("STATUS_CHANGED"),
                isNull(),
                isNull()
        );
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
                eq("STATUS_CHANGED"),
                isNull(),
                isNull()
        );
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
                eq("STATUS_CHANGED"),
                eq("Working on it"),
                isNull()
        );
    }
}
