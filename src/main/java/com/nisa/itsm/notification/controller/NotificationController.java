package com.nisa.itsm.notification.controller;

import com.nisa.itsm.notification.dto.response.NotificationResponse;
import com.nisa.itsm.notification.dto.response.UnreadNotificationCountResponse;
import com.nisa.itsm.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.Principal;
import org.springframework.data.domain.Page;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification Controller", description = "User notification management")
public class NotificationController {

        private final NotificationService notificationService;

        @GetMapping
        @Operation(summary = "Get user notifications", description = "Retrieves all notifications for the authenticated user")
        @ApiResponse(responseCode = "200", description = "Successfully retrieved notifications")
        public ResponseEntity<List<NotificationResponse>> getMyNotifications(
                        Principal principal) {
                return ResponseEntity.ok(
                                notificationService.getMyNotifications(principal.getName()));
        }

        @GetMapping(params = { "page", "size" })
        @Operation(summary = "Get paginated user notifications", description = "Retrieves a paginated list of notifications")
        @ApiResponse(responseCode = "200", description = "Successfully retrieved paginated notifications")
        public ResponseEntity<Page<NotificationResponse>> getMyNotificationsPaged(
                        Principal principal,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size) {
                return ResponseEntity.ok(
                                notificationService.getMyNotificationsPaged(principal.getName(), page, size));
        }

        @PutMapping("/{id}/read")
        @Operation(summary = "Mark notification as read", description = "Marks a specific notification as read")
        @ApiResponse(responseCode = "204", description = "Successfully marked as read")
        public ResponseEntity<Void> markAsRead(
                        @PathVariable Long id,
                        Principal principal) {
                notificationService.markAsRead(id, principal.getName());
                return ResponseEntity.noContent().build();
        }

        @PutMapping("/mark-all-read")
        @Operation(summary = "Mark all notifications as read", description = "Marks all unread notifications as read for the user")
        @ApiResponse(responseCode = "204", description = "Successfully marked all as read")
        public ResponseEntity<Void> markAllAsRead(
                        Principal principal) {
                notificationService.markAllAsRead(principal.getName());
                return ResponseEntity.noContent().build();
        }

        @GetMapping("/count")
        @Operation(summary = "Get unread count", description = "Retrieves the count of unread notifications")
        @ApiResponse(responseCode = "200", description = "Successfully retrieved count")
        public ResponseEntity<UnreadNotificationCountResponse> getUnreadCount(
                        Principal principal) {
                return ResponseEntity.ok(
                                notificationService.getUnreadCount(principal.getName()));
        }
}
