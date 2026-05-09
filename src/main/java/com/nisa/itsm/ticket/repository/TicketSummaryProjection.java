package com.nisa.itsm.ticket.repository;

public interface TicketSummaryProjection {
    Long getTotalTickets();

    Long getOpenTickets();

    Long getInProgressTickets();

    Long getResolvedTickets();

    Long getClosedTickets();

    Long getHighPriorityTickets();

    Long getCriticalPriorityTickets();
}
