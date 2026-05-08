package com.nisa.itsm.workflow.service;

import com.nisa.itsm.common.enums.TicketStatus;
import com.nisa.itsm.ticket.entity.Ticket;
import com.nisa.itsm.user.entity.User;

/**
 * Service for managing workflow history records.
 */
public interface WorkflowHistoryService {

    /**
     * Records a transition in the workflow history.
     *
     * @param ticket      The ticket that transitioned
     * @param fromStatus  The previous status
     * @param toStatus    The new status
     * @param action      The action performed
     * @param comment     Optional comment provided during transition
     * @param performedBy The user who performed the action
     */
    void recordTransition(
            Ticket ticket,
            TicketStatus fromStatus,
            TicketStatus toStatus,
            String action,
            String comment,
            User performedBy);
}
