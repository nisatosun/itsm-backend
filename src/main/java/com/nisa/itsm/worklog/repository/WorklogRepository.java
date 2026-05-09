package com.nisa.itsm.worklog.repository;

import com.nisa.itsm.worklog.entity.Worklog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface WorklogRepository extends JpaRepository<Worklog, Long> {

    List<Worklog> findByTicketId(Long ticketId);

    List<Worklog> findByUserId(Long userId); // toplam dakika hesaplamak için lazım

    int countByUserId(Long userId); // kaç worklog entry var

    @Query("SELECT w.user.id as agentId, SUM(w.minutesSpent) as totalMinutes " +
            "FROM Worklog w WHERE w.user.id IN :agentIds GROUP BY w.user.id")
    List<AgentWorklogProjection> getAgentWorklogMetrics(@Param("agentIds") List<Long> agentIds);
}
