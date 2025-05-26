package com.example.taskmanager.controller;

import com.example.taskmanager.controller.NotificationController;
import com.example.taskmanager.model.Notification;
import com.example.taskmanager.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.beans.factory.annotation.Autowired;

@WebMvcTest(NotificationController.class)
@Import(NotificationControllerTest.Config.class)
public class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NotificationService notificationService;

    @TestConfiguration
    static class Config {
        @Bean
        public NotificationService notificationService() {
            return Mockito.mock(NotificationService.class);
        }
    }

    @Test
    void testGetAllNotifications() throws Exception {
        Notification n1 = Notification.builder()
                .notificationId(1L)
                .userId(42L)
                .message("Task 1 due soon")
                .isRead(false)
                .build();

        Mockito.when(notificationService.getAllNotificationsByUserId(42L)).thenReturn(List.of(n1));

        mockMvc.perform(get("/api/v1/users/42/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].notificationId").value(1L))
                .andExpect(jsonPath("$[0].message").value("Task 1 due soon"));
    }

    @Test
    void testGetPendingNotifications() throws Exception {
        Notification n2 = Notification.builder()
                .notificationId(2L)
                .userId(42L)
                .message("Unread notification")
                .isRead(false)
                .build();

        Mockito.when(notificationService.getPendingNotificationsByUserId(42L)).thenReturn(List.of(n2));

        mockMvc.perform(get("/api/v1/users/42/notifications/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].notificationId").value(2L))
                .andExpect(jsonPath("$[0].read").value(false));
    }
}
