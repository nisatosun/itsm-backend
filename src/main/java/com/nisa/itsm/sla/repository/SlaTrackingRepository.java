package com.nisa.itsm.sla.repository;

import com.nisa.itsm.sla.entity.SlaTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SlaTrackingRepository extends JpaRepository<SlaTracking, Long> {

    Optional<SlaTracking> findByTicketId(Long ticketId);

    List<SlaTracking> findByBreachedFalseAndDueDateBetween(
            LocalDateTime now,
            LocalDateTime warningLimit
    );
}
