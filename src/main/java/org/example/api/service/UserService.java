package org.example.api.service;

import lombok.RequiredArgsConstructor;
import org.example.api.entity.User;
import org.example.api.exception.ResourceNotFoundException;
import org.example.api.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;

    public User getUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("The user is not found with id: " + userId));
    }

    public User validateCredentials(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(user -> passwordMatches(password, user.getPassword()))
                .orElseThrow(() -> new ResourceNotFoundException("Invalid username or password"));
    }

    private boolean passwordMatches(String rawPassword, String storedPassword) {
        // TODO: Replace with proper password hashing
        return storedPassword.equals(rawPassword);
    }
}