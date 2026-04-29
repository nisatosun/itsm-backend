package com.nisa.itsm.comment.controller;

import com.nisa.itsm.comment.dto.request.CreateCommentRequest;
import com.nisa.itsm.comment.dto.response.CommentResponse;
import com.nisa.itsm.comment.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/api/tickets/{id}/comments")
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable Long id,
            @Valid @RequestBody CreateCommentRequest request,
            Principal principal,
            Authentication authentication) {

        return ResponseEntity.ok(
                commentService.addComment(id, request, principal.getName(), authentication)
        );
    }

    @GetMapping("/api/tickets/{id}/comments")
    public ResponseEntity<List<CommentResponse>> getComments(
            @PathVariable Long id,
            Principal principal,
            Authentication authentication) {

        return ResponseEntity.ok(
                commentService.getComments(id, principal.getName(), authentication)
        );
    }

    @DeleteMapping("/api/comments/{id}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long id,
            Authentication authentication) {

        commentService.deleteComment(id, authentication);
        return ResponseEntity.ok().build();
    }
}
