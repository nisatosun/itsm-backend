package com.nisa.itsm.comment.controller;

import com.nisa.itsm.comment.dto.request.CreateCommentRequest;
import com.nisa.itsm.comment.dto.response.CommentResponse;
import com.nisa.itsm.comment.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Comment Controller", description = "Ticket communication and comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/api/tickets/{id}/comments")
    @Operation(summary = "Add comment", description = "Adds a comment to a ticket. Internal comments are hidden from customers.")
    @ApiResponse(responseCode = "200", description = "Comment successfully added")
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
    @Operation(summary = "Get comments", description = "Retrieves comments for a ticket based on user visibility rules")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved comments")
    public ResponseEntity<List<CommentResponse>> getComments(
            @PathVariable Long id,
            Principal principal,
            Authentication authentication) {

        return ResponseEntity.ok(
                commentService.getComments(id, principal.getName(), authentication)
        );
    }

    @DeleteMapping("/api/comments/{id}")
    @Operation(summary = "Delete comment", description = "Deletes a specific comment")
    @ApiResponse(responseCode = "200", description = "Comment successfully deleted")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long id,
            Authentication authentication) {

        commentService.deleteComment(id, authentication);
        return ResponseEntity.ok().build();
    }
}
