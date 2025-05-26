package com.example.taskmanager.service;

import com.example.taskmanager.model.Notification;

import java.util.List;

public interface NotificationService {

    List<Notification> getAllNotificationsByUserId(Long userId);

    List<Notification> getPendingNotificationsByUserId(Long userId);

    Notification createNotificationForUser(Long userId, String message);

    void markNotificationAsRead(Long userId, Long notificationId);

    void softDeleteNotification(Long userId, Long notificationId);
}