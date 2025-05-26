package com.example.taskmanager.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Task {
    private Long id;
    private Long userId;
    private String description;
    private LocalDateTime creationDate;
    private LocalDateTime targetDate;
    private boolean deleted = false;
    private boolean completed = false;
}