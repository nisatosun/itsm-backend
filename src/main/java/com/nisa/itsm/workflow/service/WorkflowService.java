package com.nisa.itsm.workflow.service;

import com.nisa.itsm.common.enums.Role;
import com.nisa.itsm.common.enums.TicketStatus;
import com.nisa.itsm.common.exception.ResourceNotFoundException;
import com.nisa.itsm.ticket.entity.Ticket;
import com.nisa.itsm.ticket.repository.TicketRepository;
import com.nisa.itsm.user.entity.User;
import com.nisa.itsm.user.repository.UserRepository;
import com.nisa.itsm.exception.custom.BadRequestException;
import com.nisa.itsm.workflow.dto.request.WorkflowTransitionRequest;
import com.nisa.itsm.workflow.dto.response.WorkflowHistoryResponse;
import com.nisa.itsm.workflow.entity.WorkflowHistory;
import com.nisa.itsm.workflow.repository.WorkflowHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import com.nisa.itsm.notification.service.NotificationService;
import com.nisa.itsm.audit.service.AuditLogService;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WorkflowService {

        private final TicketRepository ticketRepository;
        private final UserRepository userRepository;
        private final WorkflowHistoryRepository workflowHistoryRepository;
        private final NotificationService notificationService;
        private final AuditLogService auditLogService;

        public Long startTicketProcess(Map<String, Object> variables) {

                System.out.println("Workflow started for ticket: "
                                + variables.get("ticketId"));

                return System.currentTimeMillis();
        }

        public WorkflowHistoryResponse transitionTicket(
                        Long ticketId,
                        WorkflowTransitionRequest request,
                        Authentication authentication) {

                Ticket ticket = ticketRepository.findById(ticketId)
                                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));

                User user = userRepository.findByUsername(authentication.getName())
                                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

                validateTransition(ticket.getStatus(), request.getTargetStatus());

                if (request.getTargetStatus() == TicketStatus.RESOLVED
                                && (request.getComment() == null || request.getComment().isBlank())) {
                        throw new BadRequestException("Resolution note is required when resolving a ticket");
                }

                TicketStatus oldStatus = ticket.getStatus();
                boolean isReopen = (oldStatus == TicketStatus.RESOLVED
                                && request.getTargetStatus() == TicketStatus.IN_PROGRESS);

                ticket.setStatus(request.getTargetStatus());

                if (isReopen) {
                        ticket.setResolvedAt(null);
                }

                notificationService.createStatusChangedNotification(
                                ticket.getRequester(),
                                ticket);

                ticketRepository.save(ticket);

                auditLogService.logAction(
                                "TICKET",
                                ticket.getId(),
                                "STATUS_CHANGED",
                                user.getId(),
                                "Ticket status changed via workflow",
                                oldStatus.name(),
                                request.getTargetStatus().name());

                String action = isReopen ? "TICKET_REOPENED" : "STATUS_TRANSITION";

                WorkflowHistory history = WorkflowHistory.builder()
                                .ticket(ticket)
                                .fromStatus(oldStatus)
                                .toStatus(request.getTargetStatus())
                                .action(action)
                                .comment(request.getTargetStatus() == TicketStatus.RESOLVED
                                                ? request.getComment().trim()
                                                : request.getComment())
                                .performedBy(user)
                                .performedByUsername(user.getUsername())
                                .build();

                history = workflowHistoryRepository.save(history);

                return mapToResponse(history);
        }

        public List<WorkflowHistoryResponse> getTicketWorkflowHistory(
                        Long ticketId,
                        Authentication authentication) {

                User user = userRepository.findByUsername(authentication.getName())
                                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

                Ticket ticket = ticketRepository.findById(ticketId)
                                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));

                boolean isAdmin = user.getRoles().contains(Role.ADMIN);

                boolean isManager = user.getRoles().contains(Role.MANAGER);

                boolean isAssignedAgent = ticket.getAssignee() != null
                                && ticket.getAssignee().getId().equals(user.getId());

                if (!isAdmin && !isManager && !isAssignedAgent) {
                        throw new RuntimeException("Access denied");
                }

                return workflowHistoryRepository
                                .findByTicketIdOrderByCreatedAtAsc(ticketId)
                                .stream()
                                .map(this::mapToResponse)
                                .toList();
        }

        private WorkflowHistoryResponse mapToResponse(
                        WorkflowHistory history) {

                return WorkflowHistoryResponse.builder()
                                .id(history.getId())
                                .ticketId(history.getTicket().getId())
                                .fromStatus(history.getFromStatus())
                                .toStatus(history.getToStatus())
                                .action(history.getAction())
                                .comment(history.getComment())
                                .performedById(history.getPerformedBy().getId())
                                .performedByUsername(history.getPerformedByUsername())
                                .createdAt(history.getCreatedAt())
                                .build();
        }

        private void validateTransition(
                        TicketStatus current,
                        TicketStatus target) {

                switch (current) {

                        case NEW -> {
                                if (target != TicketStatus.TRIAGE) {
                                        throw new IllegalArgumentException(
                                                        "Invalid transition from NEW");
                                }
                        }

                        case TRIAGE -> {
                                if (target != TicketStatus.ASSIGNED) {
                                        throw new IllegalArgumentException(
                                                        "Invalid transition from TRIAGE");
                                }
                        }

                        case ASSIGNED -> {
                                if (target != TicketStatus.IN_PROGRESS) {
                                        throw new IllegalArgumentException(
                                                        "Invalid transition from ASSIGNED");
                                }
                        }

                        case IN_PROGRESS -> {
                                if (target != TicketStatus.WAITING_FOR_CUSTOMER
                                                && target != TicketStatus.RESOLVED) {

                                        throw new IllegalArgumentException(
                                                        "Invalid transition from IN_PROGRESS");
                                }
                        }

                        case WAITING_FOR_CUSTOMER -> {
                                if (target != TicketStatus.IN_PROGRESS) {

                                        throw new IllegalArgumentException(
                                                        "Invalid transition from WAITING_FOR_CUSTOMER");
                                }
                        }

                        case RESOLVED -> {
                                if (target != TicketStatus.CLOSED
                                                && target != TicketStatus.IN_PROGRESS) {

                                        throw new IllegalArgumentException(
                                                        "Invalid transition from RESOLVED");
                                }
                        }

                        case CLOSED -> throw new IllegalArgumentException(
                                        "Closed ticket cannot transition");
                }
        }
}
