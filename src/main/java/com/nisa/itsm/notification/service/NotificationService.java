package com.nisa.itsm.notification.service;

import com.nisa.itsm.common.exception.ResourceNotFoundException;
import com.nisa.itsm.notification.dto.response.NotificationResponse;
import com.nisa.itsm.notification.dto.response.UnreadNotificationCountResponse;
import com.nisa.itsm.notification.entity.Notification;
import com.nisa.itsm.notification.repository.NotificationRepository;
import com.nisa.itsm.user.entity.User;
import com.nisa.itsm.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.nisa.itsm.ticket.entity.Ticket;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public List<NotificationResponse> getMyNotifications(String username) {
        User user = getUser(username);

        return notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public void markAsRead(Long notificationId, String username) {
        User user = getUser(username);

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        if (!notification.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    public void markAllAsRead(String username) {
        User user = getUser(username);

        List<Notification> notifications =
                notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId());

        notifications.forEach(notification -> notification.setRead(true));

        notificationRepository.saveAll(notifications);
    }

    public UnreadNotificationCountResponse getUnreadCount(String username) {
        User user = getUser(username);

        long count = notificationRepository.countByUserIdAndIsReadFalse(user.getId());

        return new UnreadNotificationCountResponse(count);
    }

    public void createNotification(User user, String title, String message, String type) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setRead(false);

        notificationRepository.save(notification);
    }

    public void createStatusChangedNotification(
            User user,
            Ticket ticket
    ) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle("Ticket Status Updated");
        notification.setMessage(
                "Ticket " + ticket.getTicketNo()
                        + " status changed to "
                        + ticket.getStatus()
        );
        notification.setType("STATUS_CHANGED");
        notification.setRead(false);

        notificationRepository.save(notification);
    }

    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private NotificationResponse toResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .type(notification.getType())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }

    public void createCommentNotification(
            User user,
            Ticket ticket
    ) {

        Notification notification = new Notification();

        notification.setUser(user);
        notification.setTitle("New Ticket Comment");
        notification.setMessage(
                "New comment added to ticket "
                        + ticket.getTicketNo()
        );
        notification.setType("COMMENT_ADDED");
        notification.setRead(false);

        notificationRepository.save(notification);
    }

    public void createSlaRiskNotification(
            User user,
            Ticket ticket
    ) {
        if (user == null) {
            return;
        }

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle("SLA Risk");
        notification.setMessage(
                "Ticket " + ticket.getTicketNo()
                        + " is close to SLA breach."
        );
        notification.setType("SLA_RISK");
        notification.setRead(false);

        notificationRepository.save(notification);
    }
}
