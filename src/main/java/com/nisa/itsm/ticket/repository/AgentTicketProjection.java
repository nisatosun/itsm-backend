package com.nisa.itsm.ticket.repository;

public interface AgentTicketProjection {
    Long getAgentId();

    Long getAssignedCount();

    Long getResolvedCount();
}
