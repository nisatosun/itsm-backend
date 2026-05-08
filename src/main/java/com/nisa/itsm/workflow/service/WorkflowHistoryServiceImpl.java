package com.nisa.itsm.workflow.service;

import com.nisa.itsm.common.enums.TicketStatus;
import com.nisa.itsm.ticket.entity.Ticket;
import com.nisa.itsm.user.entity.User;
import com.nisa.itsm.workflow.entity.WorkflowHistory;
import com.nisa.itsm.workflow.repository.WorkflowHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowHistoryServiceImpl implements WorkflowHistoryService {

        private final WorkflowHistoryRepository workflowHistoryRepository;

        @Override
        @Transactional
        public void recordTransition(Ticket ticket, TicketStatus fromStatus, TicketStatus toStatus, String action,
                        String comment, User performedBy) {

                log.info("Recording transition for ticket {}: {} -> {} by {}",
                                ticket.getId(), fromStatus, toStatus, performedBy.getUsername());

                WorkflowHistory history = WorkflowHistory.builder()
                                .ticket(ticket)
                                .fromStatus(fromStatus)
                                .toStatus(toStatus)
                                .action(action)
                                .comment(comment)
                                .performedBy(performedBy)
                                .performedByUsername(performedBy.getUsername())
                                .build();

                workflowHistoryRepository.save(history);
        }

        @Override
        @Transactional(readOnly = true)
        public java.util.List<com.nisa.itsm.workflow.dto.response.WorkflowHistoryResponse> getHistoryForTicket(
                        Long ticketId) {
                return workflowHistoryRepository.findByTicketIdOrderByCreatedAtAsc(ticketId).stream()
                                .map(history -> com.nisa.itsm.workflow.dto.response.WorkflowHistoryResponse.builder()
                                                .id(history.getId())
                                                .ticketId(history.getTicket().getId())
                                                .fromStatus(history.getFromStatus())
                                                .toStatus(history.getToStatus())
                                                .action(history.getAction())
                                                .comment(history.getComment())
                                                .performedById(history.getPerformedBy().getId())
                                                .performedByUsername(history.getPerformedByUsername())
                                                .createdAt(history.getCreatedAt())
                                                .build())
                                .toList();
        }
}
