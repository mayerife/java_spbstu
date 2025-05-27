package com.example.taskmanager.model;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    private Long taskId;

    @NonNull
    private String taskText;

    private LocalDateTime dueDate;

    @Builder.Default
    private LocalDateTime creationDate = LocalDateTime.now();

    @Builder.Default
    private Boolean isComplete = false;

    @NonNull
    private Long userId;

    @Builder.Default
    private Boolean isDeleted = false;
}