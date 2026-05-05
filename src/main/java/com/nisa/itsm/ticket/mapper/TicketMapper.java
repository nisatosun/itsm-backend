package com.nisa.itsm.ticket.mapper;

import com.nisa.itsm.ticket.dto.response.TicketDetailResponse;
import com.nisa.itsm.ticket.dto.response.TicketSummaryResponse;
import com.nisa.itsm.ticket.entity.Ticket;
import org.springframework.stereotype.Component;
import com.nisa.itsm.sla.entity.SlaTracking;
import java.time.Duration;
import java.time.LocalDateTime;
import com.nisa.itsm.sla.entity.SlaTracking;

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

        SlaTracking tracking = ticket.getSlaTracking();

        if (tracking != null) {
            response.setSlaDueDate(tracking.getDueDate());
            response.setSlaBreached(tracking.getBreached());

            long remaining = Duration.between(LocalDateTime.now(), tracking.getDueDate()).toMinutes();
            remaining = Math.max(remaining, 0);

            response.setSlaRemainingMinutes(remaining);

            if (Boolean.TRUE.equals(tracking.getBreached())) {
                response.setSlaWarningLevel("BREACHED");
            } else if (remaining <= 60) {
                response.setSlaWarningLevel("CRITICAL");
            } else if (remaining <= 240) {
                response.setSlaWarningLevel("WARNING");
            } else {
                response.setSlaWarningLevel("NONE");
            }
        }

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


        SlaTracking tracking = ticket.getSlaTracking();

        if (tracking != null) {
            response.setSlaDueDate(tracking.getDueDate());
            response.setSlaBreached(tracking.getBreached());

            long remaining = Duration.between(LocalDateTime.now(), tracking.getDueDate()).toMinutes();
            remaining = Math.max(remaining, 0);

            response.setSlaRemainingMinutes(remaining);

            if (Boolean.TRUE.equals(tracking.getBreached())) {
                response.setSlaWarningLevel("BREACHED");
            } else if (remaining <= 60) {
                response.setSlaWarningLevel("CRITICAL");
            } else if (remaining <= 240) {
                response.setSlaWarningLevel("WARNING");
            } else {
                response.setSlaWarningLevel("NONE");
            }
        }

        return response;
    }
}
