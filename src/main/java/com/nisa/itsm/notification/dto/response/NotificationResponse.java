package com.nisa.itsm.notification.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record NotificationResponse(
        Long id,
        String title,
        String message,
        String type,
        boolean isRead,
        LocalDateTime createdAt
) {
}