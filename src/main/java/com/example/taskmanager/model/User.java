package com.example.taskmanager.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private Long userId;

    @NonNull
    private String username;

    @NonNull
    private String password;

}