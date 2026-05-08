package com.nisa.itsm.workflow.engine;

import com.nisa.itsm.common.enums.TicketStatus;
import com.nisa.itsm.ticket.entity.Ticket;
import com.nisa.itsm.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WorkflowEngineServiceImplTest {

    private WorkflowEngineServiceImpl workflowEngineService;

    @BeforeEach
    void setUp() {
        workflowEngineService = new WorkflowEngineServiceImpl();
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
}
