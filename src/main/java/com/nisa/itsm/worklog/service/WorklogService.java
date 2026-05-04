package com.nisa.itsm.worklog.service;

import com.nisa.itsm.exception.custom.CustomAccessDeniedException;
import com.nisa.itsm.common.exception.ResourceNotFoundException;
import com.nisa.itsm.ticket.entity.Ticket;
import com.nisa.itsm.ticket.repository.TicketRepository;
import com.nisa.itsm.user.entity.User;
import com.nisa.itsm.user.repository.UserRepository;
import com.nisa.itsm.worklog.dto.request.CreateWorklogRequest;
import com.nisa.itsm.worklog.dto.response.WorklogResponse;
import com.nisa.itsm.worklog.entity.Worklog;
import com.nisa.itsm.worklog.repository.WorklogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.nisa.itsm.audit.entity.AuditLog;
import com.nisa.itsm.audit.service.AuditService;
import java.time.LocalDateTime;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorklogService {

    private final WorklogRepository worklogRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;

    @Transactional
    public WorklogResponse createWorklog(Long ticketId, CreateWorklogRequest request, String username) {
        User currentUser = findCurrentUser(username);
        Ticket ticket = findTicket(ticketId);

        validateWorklogAccess(ticket, currentUser);

        Worklog worklog = Worklog.builder()
                .ticket(ticket)
                .user(currentUser)
                .minutesSpent(request.getMinutesSpent())
                .description(request.getDescription())
                .build();

        Worklog savedWorklog = worklogRepository.save(worklog);

        auditService.save(
                AuditLog.builder()
                        .entityType("WORKLOG")
                        .entityId(savedWorklog.getId())
                        .action("WORKLOG_ADDED")
                        .performedBy(currentUser.getId())
                        .details("Worklog added: " + savedWorklog.getMinutesSpent() + " minutes")
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        return mapToResponse(savedWorklog);
    }

    @Transactional(readOnly = true)
    public List<WorklogResponse> getWorklogsByTicket(Long ticketId, String username) {
        User currentUser = findCurrentUser(username);
        Ticket ticket = findTicket(ticketId);

        validateWorklogAccess(ticket, currentUser);

        return worklogRepository.findByTicketId(ticket.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private User findCurrentUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }

    private Ticket findTicket(Long ticketId) {
        return ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found: " + ticketId));
    }

    private void validateWorklogAccess(Ticket ticket, User currentUser) {
        if (hasRole(currentUser, "ADMIN") || hasRole(currentUser, "MANAGER")) {
            return;
        }

        if (hasRole(currentUser, "CUSTOMER")) {
            throw new CustomAccessDeniedException("Customers cannot access worklogs");
        }

        if (hasRole(currentUser, "AGENT")) {
            if (ticket.getAssignee() == null || !ticket.getAssignee().getId().equals(currentUser.getId())) {
                throw new CustomAccessDeniedException("Agent can only access assigned ticket worklogs");
            }
            return;
        }

        throw new CustomAccessDeniedException("You are not allowed to access worklogs");
    }

    private boolean hasRole(User user, String roleName) {
        return user.getRoles()
                .stream()
                .anyMatch(role ->
                        role.name().equals(roleName) ||
                                role.name().equals("ROLE_" + roleName)
                );
    }

    private WorklogResponse mapToResponse(Worklog worklog) {
        return WorklogResponse.builder()
                .id(worklog.getId())
                .ticketId(worklog.getTicket().getId())
                .userId(worklog.getUser().getId())
                .username(worklog.getUser().getUsername())
                .minutesSpent(worklog.getMinutesSpent())
                .description(worklog.getDescription())
                .createdAt(worklog.getCreatedAt())
                .build();
    }
}
