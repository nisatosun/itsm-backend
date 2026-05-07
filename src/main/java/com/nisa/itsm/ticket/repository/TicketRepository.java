package com.nisa.itsm.ticket.repository;

import com.nisa.itsm.ticket.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import com.nisa.itsm.common.enums.Priority;
import com.nisa.itsm.common.enums.TicketStatus;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;

public interface TicketRepository extends
        JpaRepository<Ticket, Long>,
        JpaSpecificationExecutor<Ticket> {

    @Override
    @EntityGraph(attributePaths = { "category", "requester", "assignee", "slaTracking" })
    Page<Ticket> findAll(Specification<Ticket> spec, Pageable pageable);

    List<Ticket> findAllByRequesterIdOrderByCreatedAtDesc(Long requesterId);

    List<Ticket> findAllByAssigneeIdOrderByCreatedAtDesc(Long assigneeId);

    long countByStatus(TicketStatus status);

    long countByPriority(Priority priority);

    long countByStatusIn(List<TicketStatus> statuses);

    long countByCreatedAtAfter(LocalDateTime dateTime);

    Page<Ticket> findAll(Pageable pageable);

    Page<Ticket> findAllByAssigneeId(Long assigneeId, Pageable pageable);

    Page<Ticket> findAllByRequesterId(Long requesterId, Pageable pageable);

    Page<Ticket> findAllByStatus(TicketStatus status, Pageable pageable);

    Page<Ticket> findAllByAssigneeIdAndStatus(Long assigneeId, TicketStatus status, Pageable pageable);

    Page<Ticket> findAllByRequesterIdAndStatus(Long requesterId, TicketStatus status, Pageable pageable);

    Page<Ticket> findAllByCategoryId(Long categoryId, Pageable pageable);

    Page<Ticket> findAllByAssigneeIdAndCategoryId(Long assigneeId, Long categoryId, Pageable pageable);

    Page<Ticket> findAllByRequesterIdAndCategoryId(Long requesterId, Long categoryId, Pageable pageable);
}
