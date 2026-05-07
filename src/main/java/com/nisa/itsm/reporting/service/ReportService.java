package com.nisa.itsm.reporting.service;

import com.nisa.itsm.common.enums.Priority;
import com.nisa.itsm.common.enums.TicketStatus;
import com.nisa.itsm.reporting.dto.response.AgentPerformanceReportResponse;
import com.nisa.itsm.reporting.dto.response.SlaComplianceReportResponse;
import com.nisa.itsm.reporting.dto.response.TicketSummaryReportResponse;
import com.nisa.itsm.reporting.dto.response.TicketTrendReportResponse;
import com.nisa.itsm.sla.repository.SlaTrackingRepository;
import com.nisa.itsm.ticket.entity.Ticket;
import com.nisa.itsm.ticket.repository.TicketRepository;
import com.nisa.itsm.user.entity.User;
import com.nisa.itsm.user.repository.UserRepository;
import com.nisa.itsm.worklog.entity.Worklog;
import com.nisa.itsm.worklog.repository.WorklogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final TicketRepository ticketRepository;
    private final SlaTrackingRepository slaTrackingRepository;
    private final WorklogRepository worklogRepository;
    private final UserRepository userRepository;

    public TicketSummaryReportResponse getTicketSummary() {

        long totalTickets = ticketRepository.count();

        long openTickets =
                ticketRepository.countByStatus(TicketStatus.NEW);

        long inProgressTickets =
                ticketRepository.countByStatus(TicketStatus.IN_PROGRESS);

        long resolvedTickets =
                ticketRepository.countByStatus(TicketStatus.RESOLVED);

        long closedTickets =
                ticketRepository.countByStatus(TicketStatus.CLOSED);

        long highPriorityTickets =
                ticketRepository.countByPriority(Priority.HIGH);

        long criticalPriorityTickets =
                ticketRepository.countByPriority(Priority.CRITICAL);

        return TicketSummaryReportResponse.builder()
                .totalTickets(totalTickets)
                .openTickets(openTickets)
                .inProgressTickets(inProgressTickets)
                .resolvedTickets(resolvedTickets)
                .closedTickets(closedTickets)
                .highPriorityTickets(highPriorityTickets)
                .criticalPriorityTickets(criticalPriorityTickets)
                .build();
    }

    public SlaComplianceReportResponse getSlaCompliance() {

        long breachedTickets =
                slaTrackingRepository.countByBreachedTrue();

        long nonBreachedTickets =
                slaTrackingRepository.countByBreachedFalse();

        long totalTrackedTickets =
                breachedTickets + nonBreachedTickets;

        double complianceRate = 0;

        if (totalTrackedTickets > 0) {
            complianceRate =
                    ((double) nonBreachedTickets / totalTrackedTickets) * 100;
        }

        return SlaComplianceReportResponse.builder()
                .totalTrackedTickets(totalTrackedTickets)
                .breachedTickets(breachedTickets)
                .nonBreachedTickets(nonBreachedTickets)
                .complianceRate(complianceRate)
                .build();
    }

    public TicketTrendReportResponse getTicketTrends() {

        LocalDateTime now = LocalDateTime.now();

        long createdToday =
                ticketRepository.countByCreatedAtAfter(
                        now.minusDays(1)
                );

        long createdLast7Days =
                ticketRepository.countByCreatedAtAfter(
                        now.minusDays(7)
                );

        long createdLast30Days =
                ticketRepository.countByCreatedAtAfter(
                        now.minusDays(30)
                );

        return TicketTrendReportResponse.builder()
                .createdToday(createdToday)
                .createdLast7Days(createdLast7Days)
                .createdLast30Days(createdLast30Days)
                .build();
    }

    public List<AgentPerformanceReportResponse> getAgentPerformance() {

        List<User> users = userRepository.findAll();

        List<AgentPerformanceReportResponse> responseList =
                new ArrayList<>();

        for (User user : users) {

            boolean isAgent =
                    user.getRoles()
                            .stream()
                            .anyMatch(role ->
                                    role.name().equals("AGENT")
                            );

            if (!isAgent) {
                continue;
            }

            long assignedTicketCount =
                    ticketRepository
                            .findAllByAssigneeIdOrderByCreatedAtDesc(
                                    user.getId()
                            ).size();

            long resolvedTicketCount =
                    ticketRepository
                            .findAllByAssigneeIdOrderByCreatedAtDesc(
                                    user.getId()
                            )
                            .stream()
                            .filter(ticket ->
                                    ticket.getStatus()
                                            == TicketStatus.RESOLVED
                            )
                            .count();

            List<Worklog> worklogs =
                    worklogRepository.findByUserId(user.getId());

            int totalMinutes =
                    worklogs.stream()
                            .mapToInt(Worklog::getMinutesSpent)
                            .sum();

            AgentPerformanceReportResponse response =
                    AgentPerformanceReportResponse.builder()
                            .agentId(user.getId())
                            .agentUsername(user.getUsername())
                            .assignedTicketCount(assignedTicketCount)
                            .resolvedTicketCount(resolvedTicketCount)
                            .worklogMinutes(totalMinutes)
                            .build();

            responseList.add(response);
        }

        return responseList;
    }
}
