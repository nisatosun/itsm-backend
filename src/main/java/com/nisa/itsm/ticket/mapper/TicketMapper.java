package com.nisa.itsm.ticket.mapper;

import com.nisa.itsm.ticket.dto.response.TicketDetailResponse;
import com.nisa.itsm.ticket.dto.response.TicketSummaryResponse;
import com.nisa.itsm.ticket.entity.Ticket;
import org.springframework.stereotype.Component;

@Component
public class TicketMapper {

    public TicketSummaryResponse toSummaryResponse(Ticket ticket) {

        TicketSummaryResponse response = new TicketSummaryResponse();

        response.setId(ticket.getId());
        response.setTicketNo(ticket.getTicketNo());
        response.setTitle(ticket.getTitle());

        // Category
        if (ticket.getCategory() != null) {
            response.setCategoryId(ticket.getCategory().getId());
            response.setCategoryName(ticket.getCategory().getName());
        }

        response.setPriority(ticket.getPriority());
        response.setStatus(ticket.getStatus());

        // Requester
        if (ticket.getRequester() != null) {
            response.setRequesterId(ticket.getRequester().getId());
            response.setRequesterName(ticket.getRequester().getUsername());
        }

        // Assignee (NULL olabilir!)
        if (ticket.getAssignee() != null) {
            response.setAssigneeId(ticket.getAssignee().getId());
            response.setAssigneeName(ticket.getAssignee().getUsername());
        }

        response.setCreatedAt(ticket.getCreatedAt());
        response.setUpdatedAt(ticket.getUpdatedAt());

        return response;
    }

    public TicketDetailResponse toDetailResponse(Ticket ticket) {

        TicketDetailResponse response = new TicketDetailResponse();

        response.setId(ticket.getId());
        response.setTicketNo(ticket.getTicketNo());
        response.setTitle(ticket.getTitle());
        response.setDescription(ticket.getDescription());

        // Category
        if (ticket.getCategory() != null) {
            response.setCategoryId(ticket.getCategory().getId());
            response.setCategoryName(ticket.getCategory().getName());
        }

        response.setPriority(ticket.getPriority());
        response.setStatus(ticket.getStatus());

        // Requester
        if (ticket.getRequester() != null) {
            response.setRequesterId(ticket.getRequester().getId());
            response.setRequesterName(ticket.getRequester().getUsername());
            response.setRequesterEmail(ticket.getRequester().getEmail());
        }

        // Assignee
        if (ticket.getAssignee() != null) {
            response.setAssigneeId(ticket.getAssignee().getId());
            response.setAssigneeName(ticket.getAssignee().getUsername());
            response.setAssigneeEmail(ticket.getAssignee().getEmail());
        }

        response.setProcessInstanceId(ticket.getProcessInstanceId());

        response.setCreatedAt(ticket.getCreatedAt());
        response.setUpdatedAt(ticket.getUpdatedAt());
        response.setResolvedAt(ticket.getResolvedAt());
        response.setClosedAt(ticket.getClosedAt());

        return response;
    }
}
