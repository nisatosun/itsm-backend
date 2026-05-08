package com.nisa.itsm.workflow.service;

import com.nisa.itsm.common.enums.TicketStatus;
import com.nisa.itsm.ticket.entity.Ticket;
import com.nisa.itsm.user.entity.User;
import com.nisa.itsm.workflow.entity.WorkflowHistory;
import com.nisa.itsm.workflow.repository.WorkflowHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class WorkflowHistoryServiceImplTest {

    @Mock
    private WorkflowHistoryRepository workflowHistoryRepository;

    private WorkflowHistoryServiceImpl workflowHistoryService;

    @BeforeEach
    void setUp() {
        workflowHistoryService = new WorkflowHistoryServiceImpl(workflowHistoryRepository);
    }

    @Test
    void recordTransition_ShouldSaveWorkflowHistory() {
        Ticket ticket = new Ticket();
        ticket.setId(10L);

        User performedBy = new User();
        performedBy.setUsername("testUser");

        workflowHistoryService.recordTransition(
                ticket,
                TicketStatus.NEW,
                TicketStatus.IN_PROGRESS,
                "STATUS_UPDATE",
                "Working on it",
                performedBy);

        ArgumentCaptor<WorkflowHistory> captor = ArgumentCaptor.forClass(WorkflowHistory.class);
        verify(workflowHistoryRepository).save(captor.capture());

        WorkflowHistory savedHistory = captor.getValue();
        assertThat(savedHistory.getTicket()).isEqualTo(ticket);
        assertThat(savedHistory.getFromStatus()).isEqualTo(TicketStatus.NEW);
        assertThat(savedHistory.getToStatus()).isEqualTo(TicketStatus.IN_PROGRESS);
        assertThat(savedHistory.getAction()).isEqualTo("STATUS_UPDATE");
        assertThat(savedHistory.getComment()).isEqualTo("Working on it");
        assertThat(savedHistory.getPerformedBy()).isEqualTo(performedBy);
        assertThat(savedHistory.getPerformedByUsername()).isEqualTo("testUser");
    }
}
