package com.nisa.itsm.comment.service;

import com.nisa.itsm.audit.entity.AuditLog;
import com.nisa.itsm.audit.repository.AuditLogRepository;
import com.nisa.itsm.comment.dto.request.CreateCommentRequest;
import com.nisa.itsm.comment.dto.response.CommentResponse;
import com.nisa.itsm.comment.entity.Comment;
import com.nisa.itsm.comment.repository.CommentRepository;
import com.nisa.itsm.common.exception.ResourceNotFoundException;
import com.nisa.itsm.ticket.entity.Ticket;
import com.nisa.itsm.ticket.repository.TicketRepository;
import com.nisa.itsm.user.entity.User;
import com.nisa.itsm.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;

    @Transactional
    public CommentResponse addComment(Long ticketId, CreateCommentRequest request, String username, Authentication authentication) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));

        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        validateTicketAccess(ticket, username, authentication);

        Comment comment = Comment.builder()
                .ticket(ticket)
                .author(author)
                .content(request.getContent())
                .internal(request.getInternal())
                .build();

        Comment saved = commentRepository.save(comment);

        auditLogRepository.save(
                AuditLog.builder()
                        .entityType("TICKET")
                        .entityId(ticket.getId())
                        .action("COMMENT_ADDED")
                        .performedBy(author.getId())
                        .details("Comment added to ticket " + ticket.getId())
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getComments(Long ticketId, String username, Authentication authentication) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));

        validateTicketAccess(ticket, username, authentication);

        boolean isPrivileged =
                hasAuthority(authentication, "ADMIN")
                        || hasAuthority(authentication, "AGENT")
                        || hasAuthority(authentication, "MANAGER");

        boolean isCustomer = hasAuthority(authentication, "CUSTOMER") && !isPrivileged;

        List<Comment> comments = isCustomer
                ? commentRepository.findAllByTicketIdAndInternalFalseOrderByCreatedAtAsc(ticketId)
                : commentRepository.findAllByTicketIdOrderByCreatedAtAsc(ticketId);

        return comments.stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void deleteComment(Long commentId, Authentication authentication) {
        boolean canDelete = hasAuthority(authentication, "ADMIN") || hasAuthority(authentication, "AGENT");

        if (!canDelete) {
            throw new AccessDeniedException("Only ADMIN or AGENT can delete comments");
        }

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        User performer = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        auditLogRepository.save(
                AuditLog.builder()
                        .entityType("TICKET")
                        .entityId(comment.getTicket().getId())
                        .action("COMMENT_DELETED")
                        .performedBy(performer.getId())
                        .details("Comment deleted from ticket " + comment.getTicket().getId())
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        commentRepository.delete(comment);
    }

    private void validateTicketAccess(Ticket ticket, String username, Authentication authentication) {
        boolean isAdminOrManager = hasAuthority(authentication, "ADMIN") || hasAuthority(authentication, "MANAGER");
        boolean isAgent = hasAuthority(authentication, "AGENT");
        boolean isCustomer = hasAuthority(authentication, "CUSTOMER");

        if (isAdminOrManager) {
            return;
        }

        if (isAgent) {
            if (ticket.getAssignee() == null || !ticket.getAssignee().getUsername().equals(username)) {
                throw new AccessDeniedException("Agent can only access assigned ticket comments");
            }
            return;
        }

        if (isCustomer) {
            if (!ticket.getRequester().getUsername().equals(username)) {
                throw new AccessDeniedException("Customer can only access own ticket comments");
            }
            return;
        }

        throw new AccessDeniedException("Not allowed to access ticket comments");
    }

    private boolean hasAuthority(Authentication authentication, String authority) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(authority));
    }

    private CommentResponse toResponse(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .ticketId(comment.getTicket().getId())
                .authorId(comment.getAuthor().getId())
                .authorName(comment.getAuthor().getUsername())
                .content(comment.getContent())
                .internal(comment.getInternal())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}
