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
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

       List<Ticket> findByStatusAndResolvedAtBefore(TicketStatus status, LocalDateTime dateTime);

       @Query("SELECT t.assignee.id as agentId, " +
                     "COUNT(t.id) as assignedCount, " +
                     "SUM(CASE WHEN t.status = 'RESOLVED' THEN 1L ELSE 0L END) as resolvedCount " +
                     "FROM Ticket t WHERE t.assignee.id IN :agentIds GROUP BY t.assignee.id")
       List<AgentTicketProjection> getAgentTicketMetrics(@Param("agentIds") List<Long> agentIds);

       @Query("SELECT COUNT(t.id) as totalTickets, " +
                     "SUM(CASE WHEN t.status = 'NEW' THEN 1L ELSE 0L END) as openTickets, " +
                     "SUM(CASE WHEN t.status = 'IN_PROGRESS' THEN 1L ELSE 0L END) as inProgressTickets, " +
                     "SUM(CASE WHEN t.status = 'RESOLVED' THEN 1L ELSE 0L END) as resolvedTickets, " +
                     "SUM(CASE WHEN t.status = 'CLOSED' THEN 1L ELSE 0L END) as closedTickets, " +
                     "SUM(CASE WHEN t.priority = 'HIGH' THEN 1L ELSE 0L END) as highPriorityTickets, " +
                     "SUM(CASE WHEN t.priority = 'CRITICAL' THEN 1L ELSE 0L END) as criticalPriorityTickets " +
                     "FROM Ticket t")
       TicketSummaryProjection getTicketSummaryMetrics();
}
