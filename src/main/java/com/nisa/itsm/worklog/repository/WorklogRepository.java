package com.nisa.itsm.worklog.repository;

import com.nisa.itsm.worklog.entity.Worklog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorklogRepository extends JpaRepository<Worklog, Long> {
    List<Worklog> findByTicketId(Long ticketId);
}
