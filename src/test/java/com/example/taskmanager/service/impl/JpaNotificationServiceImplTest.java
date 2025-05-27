package com.example.taskmanager.service.impl;

import com.example.taskmanager.exceptions.NotFoundException;
import com.example.taskmanager.model.Notification;
import com.example.taskmanager.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JpaNotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private JpaNotificationServiceImpl notificationService;

    @Test
    void getAllNotificationsByUserId_shouldReturnNotifications_whenTheyExist() {
        Long userId = 1L;
        Notification notif = Notification.builder()
                .notificationId(1L)
                .userId(userId)
                .message("Hello")
                .build();

        when(notificationRepository.findByUserIdAndDeletedFalse(userId))
                .thenReturn(List.of(notif));

        List<Notification> result = notificationService.getAllNotificationsByUserId(userId);

        assertThat(result).containsExactly(notif);
    }

    @Test
    void getAllNotificationsByUserId_shouldThrow_whenNoneExist() {
        when(notificationRepository.findByUserIdAndDeletedFalse(1L))
                .thenReturn(List.of());

        assertThatThrownBy(() -> notificationService.getAllNotificationsByUserId(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("No notifications found for user");
    }

    @Test
    void getPendingNotificationsByUserId_shouldReturnPending_whenTheyExist() {
        Notification pending = Notification.builder()
                .notificationId(2L)
                .userId(1L)
                .message("Pending")
                .read(false)
                .deleted(false)
                .build();

        when(notificationRepository.findByUserIdAndDeletedFalseAndReadFalse(1L))
                .thenReturn(List.of(pending));

        List<Notification> result = notificationService.getPendingNotificationsByUserId(1L);

        assertThat(result).containsExactly(pending);
    }

    @Test
    void getPendingNotificationsByUserId_shouldThrow_whenNoneExist() {
        when(notificationRepository.findByUserIdAndDeletedFalseAndReadFalse(1L))
                .thenReturn(List.of());

        assertThatThrownBy(() -> notificationService.getPendingNotificationsByUserId(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("No pending notifications found for user");
    }

    @Test
    void createNotificationForUser_shouldSaveAndReturnNotification() {
        Notification notif = Notification.builder()
                .userId(1L)
                .message("New message")
                .build();

        when(notificationRepository.save(any(Notification.class))).thenReturn(notif);

        Notification created = notificationService.createNotificationForUser(1L, "New message");

        assertThat(created).isEqualTo(notif);
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void markNotificationAsRead_shouldSetReadTrueAndSave() {
        Notification notif = Notification.builder()
                .notificationId(1L)
                .userId(1L)
                .message("Test message")
                .read(false)
                .build();

        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notif));

        notificationService.markNotificationAsRead(1L, 1L);

        assertThat(notif.isRead()).isTrue();
        verify(notificationRepository).save(notif);
    }

    @Test
    void markNotificationAsRead_shouldThrow_whenNotFound() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificationService.markNotificationAsRead(1L, 1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Notification not found");
    }

    @Test
    void softDeleteNotification_shouldSetDeletedTrueAndSave() {
        Notification notif = Notification.builder()
                .notificationId(1L)
                .userId(1L)
                .message("Delete me")
                .deleted(false)
                .build();

        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notif));

        notificationService.softDeleteNotification(1L, 1L);

        assertThat(notif.isDeleted()).isTrue();
        verify(notificationRepository).save(notif);
    }

    @Test
    void softDeleteNotification_shouldThrow_whenNotFound() {
        when(notificationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificationService.softDeleteNotification(1L, 99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Notification not found");
    }
}
