package com.nisa.itsm.notification.controller;

import com.nisa.itsm.notification.dto.response.NotificationResponse;
import com.nisa.itsm.notification.dto.response.UnreadNotificationCountResponse;
import com.nisa.itsm.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getMyNotifications(
            Principal principal
    ) {
        return ResponseEntity.ok(
                notificationService.getMyNotifications(principal.getName())
        );
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long id,
            Principal principal
    ) {
        notificationService.markAsRead(id, principal.getName());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/mark-all-read")
    public ResponseEntity<Void> markAllAsRead(
            Principal principal
    ) {
        notificationService.markAllAsRead(principal.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count")
    public ResponseEntity<UnreadNotificationCountResponse> getUnreadCount(
            Principal principal
    ) {
        return ResponseEntity.ok(
                notificationService.getUnreadCount(principal.getName())
        );
    }
}
