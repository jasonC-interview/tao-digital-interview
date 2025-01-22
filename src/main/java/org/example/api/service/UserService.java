package org.example.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.api.entity.User;
import org.example.api.exception.ResourceNotFoundException;
import org.example.api.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
// TODO: Consider adding caching for frequently accessed user data
public class UserService {
    private final UserRepository userRepository;

    public User getUserById(UUID userId) {
        log.debug("Fetching user with id: {}", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with id: {}", userId);
                    return new ResourceNotFoundException("The user is not found with id: " + userId);
                });
    }

    public User validateCredentials(String username, String password) {
        log.debug("Validating credentials for user: {}", username);
        return userRepository.findByUsername(username)
                .filter(user -> passwordMatches(password, user.getPassword()))
                .orElseThrow(() -> {
                    log.warn("Invalid login attempt for user: {}", username);
                    return new ResourceNotFoundException("The user is not found with given username and password");
                });
    }

    private boolean passwordMatches(String rawPassword, String storedPassword) {
        // TODO: Replace with proper password hashing
        return storedPassword.equals(rawPassword);
    }
}