package com.nisa.itsm.sla.service;

import com.nisa.itsm.sla.entity.SlaPolicy;
import com.nisa.itsm.sla.entity.SlaTracking;
import com.nisa.itsm.sla.repository.SlaPolicyRepository;
import com.nisa.itsm.sla.repository.SlaTrackingRepository;
import com.nisa.itsm.ticket.entity.Ticket;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SlaService {

    private final SlaPolicyRepository policyRepository;
    private final SlaTrackingRepository trackingRepository;
    private final SlaCalculatorService calculatorService;

    public void initializeForTicket(Ticket ticket) {
        SlaPolicy policy = policyRepository
                .findByPriorityAndActiveTrue(ticket.getPriority())
                .orElseThrow(() -> new RuntimeException("SLA policy not found"));

        SlaTracking tracking = calculatorService.initializeSlaTracking(
                ticket,
                policy,
                LocalDateTime.now()
        );

        trackingRepository.save(tracking);
    }
}