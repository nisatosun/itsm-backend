package com.nisa.itsm.workflow.engine;

import com.nisa.itsm.common.enums.TicketStatus;
import com.nisa.itsm.exception.custom.BadRequestException;
import com.nisa.itsm.ticket.entity.Ticket;
import com.nisa.itsm.user.entity.User;
import com.nisa.itsm.sla.service.SlaService;
import com.nisa.itsm.workflow.service.WorkflowHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowEngineServiceImpl implements WorkflowEngineService {

    private final WorkflowHistoryService workflowHistoryService;
    private final SlaService slaService;

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
    public void executeTransition(Ticket ticket, TicketStatus targetStatus, User performedBy,
            String comment) {
        log.info("Executing transition for ticket {} to {}", ticket.getId(), targetStatus);

        if (targetStatus == TicketStatus.RESOLVED && (comment == null || comment.isBlank())) {
            throw new BadRequestException("Resolution note is required when resolving a ticket");
        }

        TicketStatus fromStatus = ticket.getStatus();
        boolean isReopen = (fromStatus == TicketStatus.RESOLVED && targetStatus == TicketStatus.IN_PROGRESS);

        ticket.setStatus(targetStatus);

        if (targetStatus == TicketStatus.RESOLVED) {
            ticket.setResolvedAt(java.time.LocalDateTime.now());
        }

        if (isReopen) {
            ticket.setResolvedAt(null);
        }

        if (targetStatus == TicketStatus.CLOSED) {
            ticket.setClosedAt(java.time.LocalDateTime.now());
        }

        slaService.handleTicketTransition(ticket, fromStatus, targetStatus);

        String action = isReopen ? "TICKET_REOPENED" : "STATUS_TRANSITION";

        workflowHistoryService.recordTransition(
                ticket,
                fromStatus,
                targetStatus,
                action,
                targetStatus == TicketStatus.RESOLVED ? comment.trim() : comment,
                performedBy);
    }

    @Override
    public void executeAssignment(Ticket ticket, User assignee) {
        log.info("Executing assignment for ticket {} to assignee {}", ticket.getId(), assignee.getId());
        ticket.setAssignee(assignee);
        TicketStatus oldStatus = ticket.getStatus();

        if (oldStatus == TicketStatus.NEW || oldStatus == TicketStatus.TRIAGE) {
            ticket.setStatus(TicketStatus.ASSIGNED);

            workflowHistoryService.recordTransition(
                    ticket,
                    oldStatus,
                    TicketStatus.ASSIGNED,
                    "STATUS_TRANSITION",
                    "Ticket moved to ASSIGNED",
                    assignee);
        }
    }
}
