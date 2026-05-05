package com.nisa.itsm.sla.service;

import com.nisa.itsm.sla.entity.SlaPolicy;
import com.nisa.itsm.sla.entity.SlaTracking;
import com.nisa.itsm.ticket.entity.Ticket;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SlaCalculatorService {

    public SlaTracking initializeSlaTracking(Ticket ticket, SlaPolicy policy, LocalDateTime startTime) {
        SlaTracking tracking = new SlaTracking();
        tracking.setTicket(ticket);
        tracking.setPolicy(policy);
        tracking.setStartTime(startTime);

        tracking.setDueDate(calculateDueDate(startTime, policy.getResolutionTimeHours()));
        tracking.setFirstResponseDueDate(calculateFirstResponseDueDate(startTime, policy.getResponseTimeHours()));

        tracking.setBreached(false);
        return tracking;
    }

    public LocalDateTime calculateDueDate(LocalDateTime startTime, int resolutionTimeHours) {
        return startTime.plusHours(resolutionTimeHours);
    }

    public LocalDateTime calculateFirstResponseDueDate(LocalDateTime startTime, int responseTimeHours) {
        return startTime.plusHours(responseTimeHours);
    }
}
