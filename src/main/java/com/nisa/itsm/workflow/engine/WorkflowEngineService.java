package com.nisa.itsm.workflow.engine;

import com.nisa.itsm.common.enums.TicketStatus;
import com.nisa.itsm.ticket.entity.Ticket;
import com.nisa.itsm.user.entity.User;
import org.springframework.security.core.Authentication;

import java.util.Map;

/**
 * Abstraction layer for the Workflow Engine.
 * Prepares the application for future jBPM/BPMN integration.
 * Acts as a facade for workflow-related operations on Tickets.
 */
public interface WorkflowEngineService {

    /**
     * Starts a workflow process for a newly created ticket.
     *
     * @param ticket    The ticket entity
     * @param variables Workflow variables
     * @return The process instance ID
     */
    Long startProcess(Ticket ticket, Map<String, Object> variables);

    /**
     * Validates if a transition between two statuses is allowed.
     *
     * @param current The current ticket status
     * @param next    The target ticket status
     * @return true if valid, false otherwise
     */
    boolean isTransitionValid(TicketStatus current, TicketStatus next);

    /**
     * Executes a status transition.
     *
     * @param ticket       The ticket to transition
     * @param targetStatus The target status
     * @param performedBy  The user performing the transition
     * @param comment      An optional comment for the transition
     */
    void executeTransition(Ticket ticket, TicketStatus targetStatus, User performedBy, String comment);

    /**
     * Handles side-effects of assigning a ticket.
     *
     * @param ticket   The ticket being assigned
     * @param assignee The user being assigned to the ticket
     */
    void executeAssignment(Ticket ticket, User assignee);
}
