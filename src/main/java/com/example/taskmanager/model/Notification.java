package com.example.taskmanager.model;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    private Long notificationId;

    @NonNull
    private Long userId;

    @NonNull
    private String message;

    @NonNull
    @Builder.Default
    private LocalDateTime creationDate = LocalDateTime.now();

    @Builder.Default
    private boolean isRead = false;

    @Builder.Default
    private boolean isDeleted = false;
}