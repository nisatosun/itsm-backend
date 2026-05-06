package com.nisa.itsm.workflow.repository;

import com.nisa.itsm.workflow.entity.WorkflowHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkflowHistoryRepository extends JpaRepository<WorkflowHistory, Long> {

    List<WorkflowHistory> findByTicketIdOrderByCreatedAtAsc(Long ticketId);
}
