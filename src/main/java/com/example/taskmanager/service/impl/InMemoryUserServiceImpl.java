package com.example.taskmanager.service.impl;

import com.example.taskmanager.exceptions.DuplicateResourceException;
import com.example.taskmanager.model.User;
import com.example.taskmanager.service.UserService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
@Profile("in-memory")
public class InMemoryUserServiceImpl implements UserService {

    private final AtomicLong idGenerator = new AtomicLong(1);

    private final Map<Long, User> usersById = new HashMap<>();
    private final Map<String, User> usersByUsername = new HashMap<>();

    @Override
    public User registerUser(User user) {
        if (usersByUsername.containsKey(user.getUsername())) {
            throw new DuplicateResourceException("Username '" + user.getUsername() + "' already exists");
        }
        long newId = idGenerator.getAndIncrement();
        user.setUserId(newId);
        usersById.put(newId, user);
        usersByUsername.put(user.getUsername(), user);
        return user;
    }

    @Override
    public Optional<User> login(String username, String password) {
        User user = usersByUsername.get(username);
        if (user != null && user.getPassword().equals(password)) {
            return Optional.of(user);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findById(Long userId) {
        return Optional.ofNullable(usersById.get(userId));
    }
}
