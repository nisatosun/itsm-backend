package com.nisa.itsm.ticket.repository;

import com.nisa.itsm.ticket.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import com.nisa.itsm.common.enums.Priority;
import com.nisa.itsm.common.enums.TicketStatus;
import java.time.LocalDateTime;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findAllByRequesterIdOrderByCreatedAtDesc(Long requesterId);

    List<Ticket> findAllByAssigneeIdOrderByCreatedAtDesc(Long assigneeId);

    long countByStatus(TicketStatus status);

    long countByPriority(Priority priority);

    long countByStatusIn(List<TicketStatus> statuses);

    long countByCreatedAtAfter(LocalDateTime dateTime);
}
