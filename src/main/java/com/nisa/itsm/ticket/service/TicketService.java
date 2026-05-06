package com.nisa.itsm.ticket.service;

import com.nisa.itsm.category.entity.Category;
import com.nisa.itsm.category.repository.CategoryRepository;
import com.nisa.itsm.common.enums.TicketStatus;
import com.nisa.itsm.common.exception.ResourceNotFoundException;
import com.nisa.itsm.ticket.dto.request.AssignTicketRequest;
import com.nisa.itsm.ticket.dto.request.CreateTicketRequest;
import com.nisa.itsm.ticket.dto.request.UpdateTicketStatusRequest;
import com.nisa.itsm.ticket.dto.response.TicketDetailResponse;
import com.nisa.itsm.ticket.dto.response.TicketSummaryResponse;
import com.nisa.itsm.ticket.entity.Ticket;
import com.nisa.itsm.ticket.mapper.TicketMapper;
import com.nisa.itsm.ticket.repository.TicketRepository;
import com.nisa.itsm.user.entity.User;
import com.nisa.itsm.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.nisa.itsm.common.enums.Role;
import com.nisa.itsm.sla.service.SlaService;
import com.nisa.itsm.workflow.service.WorkflowService;
import com.nisa.itsm.notification.service.NotificationService;
import com.nisa.itsm.audit.service.AuditLogService;

import java.time.Year;
import java.util.List;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TicketMapper ticketMapper;
    private final SlaService slaService;
    private final WorkflowService workflowService;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;

    @Transactional
    public TicketDetailResponse createTicket(CreateTicketRequest request, String username) {
        User requester = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        Ticket ticket = new Ticket();
        ticket.setTitle(request.getTitle());
        ticket.setDescription(request.getDescription());
        ticket.setCategory(category);
        ticket.setPriority(request.getPriority());
        ticket.setStatus(TicketStatus.NEW);
        ticket.setRequester(requester);
        ticket.setAssignee(null);

        ticket.setTicketNo("TEMP-" + System.currentTimeMillis());

        ticket = ticketRepository.save(ticket);

        String year = String.valueOf(Year.now().getValue());
        String ticketNo = String.format("TCK-%s-%06d", year, ticket.getId());
        ticket.setTicketNo(ticketNo);

        Map<String, Object> workflowVariables = new HashMap<>();
        workflowVariables.put("ticketId", ticket.getId());
        workflowVariables.put("ticketNo", ticket.getTicketNo());
        workflowVariables.put("priority", ticket.getPriority().name());
        workflowVariables.put("status", ticket.getStatus().name());

        Long processInstanceId =
                workflowService.startTicketProcess(workflowVariables);

        ticket.setProcessInstanceId(processInstanceId);

        ticket = ticketRepository.save(ticket);

        slaService.initializeForTicket(ticket);

        notificationService.createNotification(
                requester,
                "Ticket Created",
                "Your ticket " + ticket.getTicketNo() + " has been created.",
                "TICKET_CREATED"
        );

        return ticketMapper.toDetailResponse(ticket);
    }

    @Transactional(readOnly = true)
    public List<TicketSummaryResponse> getAllTickets() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        boolean isAdminOrManager = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN") || a.getAuthority().equals("MANAGER"));

        boolean isAgent = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("AGENT"));

        String username = auth.getName();

        List<Ticket> tickets;

        if (isAdminOrManager) {
            tickets = ticketRepository.findAll();

        } else if (isAgent) {

            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            tickets = ticketRepository.findAllByAssigneeIdOrderByCreatedAtDesc(user.getId());

        } else {
            // CUSTOMER
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            tickets = ticketRepository.findAllByRequesterIdOrderByCreatedAtDesc(user.getId());
        }

        return tickets.stream()
                .map(ticketMapper::toSummaryResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TicketSummaryResponse> getMyTickets(String username) {
        User requester = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return ticketRepository.findAllByRequesterIdOrderByCreatedAtDesc(requester.getId()).stream()
                .map(ticketMapper::toSummaryResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TicketDetailResponse getTicketByIdAndCheckAccess(Long id, String username, boolean isPrivileged) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));

        if (!isPrivileged && !ticket.getRequester().getUsername().equals(username)) {
            throw new AccessDeniedException("You do not have permission to view this ticket");
        }

        return ticketMapper.toDetailResponse(ticket);
    }

    @Transactional
    public void assignTicket(Long id, AssignTicketRequest request) {

        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        User assignee = userRepository.findById(request.getAssigneeId())
                .orElseThrow(() -> new RuntimeException("Assignee user not found"));

        if (!assignee.getRoles().contains(Role.AGENT)) {
            throw new RuntimeException("Assignee must have AGENT role");
        }

        String oldAssignee = ticket.getAssignee() != null
                ? ticket.getAssignee().getUsername()
                : "UNASSIGNED";

        ticket.setAssignee(assignee);

        String newAssignee = assignee.getUsername();

        notificationService.createNotification(
                assignee,
                "Ticket Assigned",
                "Ticket " + ticket.getTicketNo() + " assigned to you.",
                "TICKET_ASSIGNED"
        );

        if (ticket.getStatus() == TicketStatus.NEW) {
            ticket.setStatus(TicketStatus.IN_PROGRESS);
        }

        ticketRepository.save(ticket);

        auditLogService.logAction(
                "TICKET",
                ticket.getId(),
                "TICKET_ASSIGNED",
                assignee.getId(),
                "Ticket assigned",
                oldAssignee,
                newAssignee
        );
    }

    @Transactional
    public void updateTicketStatus(Long id, UpdateTicketStatusRequest request, String username,
            Authentication authentication) {

        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        boolean isPrivileged = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN") || a.getAuthority().equals("MANAGER"));

        boolean isAgent = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("AGENT"));

        if (isAgent) {
            if (ticket.getAssignee() == null ||
                    !ticket.getAssignee().getUsername().equals(username)) {
                throw new AccessDeniedException("Agent can only update assigned tickets");
            }
        }

        if (!isPrivileged && !isAgent) {
            throw new AccessDeniedException("Not allowed to update ticket status");
        }

        TicketStatus currentStatus = ticket.getStatus();
        TicketStatus newStatus = request.getStatus();

        if (!isValidTransition(currentStatus, newStatus)) {
            throw new RuntimeException("Invalid status transition: "
                    + currentStatus + " -> " + newStatus);
        }

        ticket.setStatus(newStatus);

        if (newStatus == TicketStatus.RESOLVED) {
            ticket.setResolvedAt(LocalDateTime.now());
        }

        if (newStatus == TicketStatus.CLOSED) {
            ticket.setClosedAt(LocalDateTime.now());
        }

        ticketRepository.save(ticket);
    }

    private boolean isValidTransition(TicketStatus current, TicketStatus next) {

        return switch (current) {
            case NEW -> next == TicketStatus.IN_PROGRESS;

            case IN_PROGRESS ->
                next == TicketStatus.WAITING_FOR_CUSTOMER ||
                        next == TicketStatus.RESOLVED;

            case WAITING_FOR_CUSTOMER ->
                next == TicketStatus.IN_PROGRESS;

            case RESOLVED ->
                next == TicketStatus.CLOSED ||
                        next == TicketStatus.IN_PROGRESS; // reopen

            default -> false;
        };
    }

    @Transactional
    public void reopenTicket(Long id, String username, Authentication authentication) {

        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        boolean isPrivileged = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN") || a.getAuthority().equals("MANAGER"));

        boolean isAgent = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("AGENT"));

        boolean isCustomer = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("CUSTOMER"));

        if (isAgent) {
            if (ticket.getAssignee() == null ||
                    !ticket.getAssignee().getUsername().equals(username)) {
                throw new AccessDeniedException("Agent can only reopen assigned tickets");
            }
        }

        if (isCustomer) {
            if (!ticket.getRequester().getUsername().equals(username)) {
                throw new AccessDeniedException("Customer can only reopen own tickets");
            }
        }

        if (ticket.getStatus() != TicketStatus.RESOLVED) {
            throw new RuntimeException("Only RESOLVED tickets can be reopened");
        }

        ticket.setStatus(TicketStatus.IN_PROGRESS);
        ticket.setResolvedAt(null); // 🔥 küçük refine

        ticketRepository.save(ticket);
    }

    @Transactional
    public void closeTicket(Long id, String username, Authentication authentication) {

        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        boolean isPrivileged = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN") || a.getAuthority().equals("MANAGER"));

        boolean isCustomer = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("CUSTOMER"));

        if (isCustomer) {
            if (!ticket.getRequester().getUsername().equals(username)) {
                throw new AccessDeniedException("Customer can only close own tickets");
            }
        }

        if (!isPrivileged && !isCustomer) {
            throw new AccessDeniedException("Not allowed to close ticket");
        }

        if (ticket.getStatus() != TicketStatus.RESOLVED) {
            throw new RuntimeException("Only RESOLVED tickets can be closed");
        }

        ticket.setStatus(TicketStatus.CLOSED);
        ticket.setClosedAt(LocalDateTime.now());

        ticketRepository.save(ticket);
    }
}
