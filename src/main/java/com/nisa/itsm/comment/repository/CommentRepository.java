package com.nisa.itsm.comment.repository;

import com.nisa.itsm.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // Tüm commentler (agent/admin için)
    List<Comment> findAllByTicketIdOrderByCreatedAtAsc(Long ticketId);

    // Sadece external commentler (customer için)
    List<Comment> findAllByTicketIdAndInternalFalseOrderByCreatedAtAsc(Long ticketId);
}