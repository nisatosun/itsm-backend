package com.nisa.itsm.workflow.transition;

import com.nisa.itsm.common.enums.TicketStatus;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * Policy class responsible for determining allowed status transitions.
 * Extracts the rule structure out of the service layer to make it testable and
 * modular.
 */
@Component
public class WorkflowTransitionPolicy {

    private static final Map<TicketStatus, Set<TicketStatus>> ALLOWED_TRANSITIONS = Map.of(
            TicketStatus.NEW, EnumSet.of(TicketStatus.TRIAGE),
            TicketStatus.TRIAGE, EnumSet.of(TicketStatus.ASSIGNED),
            TicketStatus.ASSIGNED, EnumSet.of(TicketStatus.IN_PROGRESS),
            TicketStatus.IN_PROGRESS, EnumSet.of(TicketStatus.WAITING_FOR_CUSTOMER, TicketStatus.RESOLVED),
            TicketStatus.WAITING_FOR_CUSTOMER, EnumSet.of(TicketStatus.IN_PROGRESS),
            TicketStatus.RESOLVED, EnumSet.of(TicketStatus.CLOSED, TicketStatus.IN_PROGRESS),
            TicketStatus.CLOSED, EnumSet.noneOf(TicketStatus.class));

    /**
     * Checks if a transition from current status to next status is allowed.
     *
     * @param current The current status of the ticket
     * @param next    The target status to transition to
     * @return true if the transition is defined as allowed, false otherwise
     */
    public boolean isAllowed(TicketStatus current, TicketStatus next) {
        if (current == null || next == null) {
            return false;
        }

        Set<TicketStatus> allowedNextStatuses = ALLOWED_TRANSITIONS.get(current);
        if (allowedNextStatuses == null) {
            return false;
        }

        return allowedNextStatuses.contains(next);
    }
}
