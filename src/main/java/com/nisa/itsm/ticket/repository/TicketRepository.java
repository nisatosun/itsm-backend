package com.nisa.itsm.ticket.repository;

import com.nisa.itsm.ticket.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findAllByRequesterIdOrderByCreatedAtDesc(Long requesterId);
}
