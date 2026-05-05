package com.nisa.itsm.sla.repository;

import com.nisa.itsm.common.enums.Priority;
import com.nisa.itsm.sla.entity.SlaPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SlaPolicyRepository extends JpaRepository<SlaPolicy, Long> {
    Optional<SlaPolicy> findByPriorityAndActiveTrue(Priority priority);
}
