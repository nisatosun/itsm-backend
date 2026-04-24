package com.nisa.itsm.ticket.service;

import com.nisa.itsm.category.entity.Category;
import com.nisa.itsm.category.repository.CategoryRepository;
import com.nisa.itsm.common.enums.TicketStatus;
import com.nisa.itsm.common.exception.ResourceNotFoundException;
import com.nisa.itsm.ticket.dto.request.CreateTicketRequest;
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

import java.time.Year;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TicketMapper ticketMapper;

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

        ticket = ticketRepository.save(ticket);

        return ticketMapper.toDetailResponse(ticket);
    }

    @Transactional(readOnly = true)
    public List<TicketSummaryResponse> getAllTickets() {
        return ticketRepository.findAll().stream()
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
}
