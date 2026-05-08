package com.nisa.itsm.workflow.engine;

import com.nisa.itsm.common.enums.TicketStatus;
import com.nisa.itsm.ticket.entity.Ticket;
import com.nisa.itsm.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class WorkflowEngineServiceImpl implements WorkflowEngineService {

    @Override
    public Long startProcess(Ticket ticket, Map<String, Object> variables) {
        log.info("Placeholder: Starting workflow process for ticket: {}", ticket.getId());
        // Currently handled by existing WorkflowService
        // Future jBPM integration will happen here
        return null;
    }

    @Override
    public boolean isTransitionValid(TicketStatus current, TicketStatus next) {
        log.info("Placeholder: Validating transition from {} to {}", current, next);
        // Logic currently resides in TicketService and WorkflowService
        return true;
    }

    @Override
    public void executeTransition(Ticket ticket, TicketStatus targetStatus, Authentication authentication,
            String comment) {
        log.info("Placeholder: Executing transition for ticket {} to {}", ticket.getId(), targetStatus);
        // Logic currently resides in TicketService and WorkflowService
    }

    @Override
    public void executeAssignment(Ticket ticket, User assignee) {
        log.info("Executing assignment for ticket {} to assignee {}", ticket.getId(), assignee.getId());
        ticket.setAssignee(assignee);
        if (ticket.getStatus() == TicketStatus.NEW) {
            ticket.setStatus(TicketStatus.IN_PROGRESS);
        }
    }
}
