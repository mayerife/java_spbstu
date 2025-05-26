package com.example.taskmanager.service.impl;

import com.example.taskmanager.exceptions.NotFoundException;
import com.example.taskmanager.model.Notification;
import com.example.taskmanager.repository.NotificationRepository;
import com.example.taskmanager.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Profile("jpa")
@RequiredArgsConstructor
public class JpaNotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public List<Notification> getAllNotificationsByUserId(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserIdAndDeletedFalse(userId);
        if (notifications.isEmpty()) {
            throw new NotFoundException("No notifications found for user " + userId);
        }
        return notifications;
    }

    @Override
    public List<Notification> getPendingNotificationsByUserId(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserIdAndDeletedFalseAndReadFalse(userId);
        if (notifications.isEmpty()) {
            throw new NotFoundException("No pending notifications found for user " + userId);
        }
        return notifications;
    }

    @Override
    public Notification createNotificationForUser(Long userId, String message) {
        Notification notification = Notification.builder()
                .userId(userId)
                .message(message)
                .build();
        return notificationRepository.save(notification);
    }

    @Override
    public void markNotificationAsRead(Long userId, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .filter(n -> n.getUserId().equals(userId))
                .orElseThrow(() -> new NotFoundException("Notification not found"));

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Override
    public void softDeleteNotification(Long userId, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .filter(n -> n.getUserId().equals(userId))
                .orElseThrow(() -> new NotFoundException("Notification not found"));

        notification.setDeleted(true);
        notificationRepository.save(notification);
    }
}
