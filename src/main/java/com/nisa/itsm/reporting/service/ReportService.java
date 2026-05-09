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

import com.nisa.itsm.common.enums.Role;
import com.nisa.itsm.ticket.repository.AgentTicketProjection;
import com.nisa.itsm.ticket.repository.TicketSummaryProjection;
import com.nisa.itsm.worklog.repository.AgentWorklogProjection;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

        private final TicketRepository ticketRepository;
        private final SlaTrackingRepository slaTrackingRepository;
        private final WorklogRepository worklogRepository;
        private final UserRepository userRepository;

        public TicketSummaryReportResponse getTicketSummary() {

                TicketSummaryProjection metrics = ticketRepository.getTicketSummaryMetrics();

                long totalTickets = metrics != null && metrics.getTotalTickets() != null ? metrics.getTotalTickets()
                                : 0;
                long openTickets = metrics != null && metrics.getOpenTickets() != null ? metrics.getOpenTickets() : 0;
                long inProgressTickets = metrics != null && metrics.getInProgressTickets() != null
                                ? metrics.getInProgressTickets()
                                : 0;
                long resolvedTickets = metrics != null && metrics.getResolvedTickets() != null
                                ? metrics.getResolvedTickets()
                                : 0;
                long closedTickets = metrics != null && metrics.getClosedTickets() != null ? metrics.getClosedTickets()
                                : 0;
                long highPriorityTickets = metrics != null && metrics.getHighPriorityTickets() != null
                                ? metrics.getHighPriorityTickets()
                                : 0;
                long criticalPriorityTickets = metrics != null && metrics.getCriticalPriorityTickets() != null
                                ? metrics.getCriticalPriorityTickets()
                                : 0;

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

        public SlaComplianceReportResponse getSlaCompliance(Integer days) {
                long breachedTickets;
                long nonBreachedTickets;

                if (days != null) {
                        LocalDateTime threshold = LocalDateTime.now().minusDays(days);
                        breachedTickets = slaTrackingRepository.countByBreachedTrueAndStartTimeAfter(threshold);
                        nonBreachedTickets = slaTrackingRepository.countByBreachedFalseAndStartTimeAfter(threshold);
                } else {
                        breachedTickets = slaTrackingRepository.countByBreachedTrue();
                        nonBreachedTickets = slaTrackingRepository.countByBreachedFalse();
                }

                long totalTrackedTickets = breachedTickets + nonBreachedTickets;

                double complianceRate = 0;

                if (totalTrackedTickets > 0) {
                        complianceRate = ((double) nonBreachedTickets / totalTrackedTickets) * 100;
                }

                return SlaComplianceReportResponse.builder()
                                .totalTrackedTickets(totalTrackedTickets)
                                .breachedTickets(breachedTickets)
                                .nonBreachedTickets(nonBreachedTickets)
                                .complianceRate(complianceRate)
                                .build();
        }

        public TicketTrendReportResponse getTicketTrends(Integer days) {

                LocalDateTime now = LocalDateTime.now();

                long createdToday = ticketRepository.countByCreatedAtAfter(
                                now.minusDays(1));

                // Note: To preserve the DTO shape, the 'createdLast7Days' variable and
                // subsequent DTO field dynamically represent the requested rolling days window.
                // For example, when ?days=30 is passed, 'createdLast7Days' actually means tickets created in the last 30 days.
                int targetDays = days != null ? days : 7;
                long createdLast7Days = ticketRepository.countByCreatedAtAfter(
                                now.minusDays(targetDays));

                long createdLast30Days = ticketRepository.countByCreatedAtAfter(
                                now.minusDays(30));

                return TicketTrendReportResponse.builder()
                                .createdToday(createdToday)
                                .createdLast7Days(createdLast7Days)
                                .createdLast30Days(createdLast30Days)
                                .build();
        }

        public List<AgentPerformanceReportResponse> getAgentPerformance() {

                List<User> agents = userRepository.findByRolesIn(Set.of(Role.AGENT));
                if (agents.isEmpty()) {
                        return Collections.emptyList();
                }

                List<Long> agentIds = agents.stream()
                                .map(User::getId)
                                .collect(Collectors.toList());

                List<AgentTicketProjection> ticketMetrics = ticketRepository.getAgentTicketMetrics(agentIds);
                List<AgentWorklogProjection> worklogMetrics = worklogRepository.getAgentWorklogMetrics(agentIds);

                Map<Long, AgentTicketProjection> ticketMap = ticketMetrics.stream()
                                .collect(Collectors.toMap(AgentTicketProjection::getAgentId, p -> p));
                Map<Long, AgentWorklogProjection> worklogMap = worklogMetrics.stream()
                                .collect(Collectors.toMap(AgentWorklogProjection::getAgentId, p -> p));

                List<AgentPerformanceReportResponse> responseList = new ArrayList<>();

                for (User agent : agents) {
                        AgentTicketProjection tProj = ticketMap.get(agent.getId());
                        AgentWorklogProjection wProj = worklogMap.get(agent.getId());

                        long assignedCount = tProj != null && tProj.getAssignedCount() != null
                                        ? tProj.getAssignedCount()
                                        : 0;
                        long resolvedCount = tProj != null && tProj.getResolvedCount() != null
                                        ? tProj.getResolvedCount()
                                        : 0;
                        int worklogMinutes = wProj != null && wProj.getTotalMinutes() != null
                                        ? wProj.getTotalMinutes().intValue()
                                        : 0;

                        AgentPerformanceReportResponse response = AgentPerformanceReportResponse.builder()
                                        .agentId(agent.getId())
                                        .agentUsername(agent.getUsername())
                                        .assignedTicketCount(assignedCount)
                                        .resolvedTicketCount(resolvedCount)
                                        .worklogMinutes(worklogMinutes)
                                        .build();

                        responseList.add(response);
                }

                return responseList;
        }
}
