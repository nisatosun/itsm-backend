package com.nisa.itsm.sla.service;

import com.nisa.itsm.notification.service.NotificationService;
import com.nisa.itsm.sla.entity.SlaTracking;
import com.nisa.itsm.sla.repository.SlaTrackingRepository;
import com.nisa.itsm.ticket.entity.Ticket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SlaMonitoringService {

    private final SlaTrackingRepository slaTrackingRepository;
    private final NotificationService notificationService;

    @Scheduled(fixedRate = 300000)
    @Transactional
    public void checkSlaRisks() {

        LocalDateTime now = LocalDateTime.now();

        LocalDateTime warningLimit = now.plusHours(1);

        List<SlaTracking> riskyTrackings =
                slaTrackingRepository.findByBreachedFalseAndDueDateBetween(
                        now,
                        warningLimit
                );

        for (SlaTracking tracking : riskyTrackings) {

            Ticket ticket = tracking.getTicket();

            if (ticket.getAssignee() == null) {
                continue;
            }

            notificationService.createSlaRiskNotification(
                    ticket.getAssignee(),
                    ticket
            );

            log.info(
                    "SLA risk notification created for ticket {}",
                    ticket.getTicketNo()
            );
        }
    }
}