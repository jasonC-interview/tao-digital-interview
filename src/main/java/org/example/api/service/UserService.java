package org.example.api.service;

import lombok.RequiredArgsConstructor;
import org.example.api.dto.LoginRequest;
import org.example.api.dto.UserDTO;
import org.example.api.exception.ResourceNotFoundException;
import org.example.api.mapper.LibraryMapper;
import org.example.api.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final LibraryMapper libraryMapper;
    private final UserRepository userRepository;

    public UserDTO login(LoginRequest loginRequest) {
        return userRepository
                .findByUsername(loginRequest.getUsername())
                .filter(user -> passwordMatches(loginRequest.getPassword(), user.getPassword()))
                .map(libraryMapper::toUserDTO)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "The user is not found with given username and password"
                ));
    }

    public UserDTO getUserById(UUID id) {
        return userRepository.findById(id)
                .map(libraryMapper::toUserDTO)
                .orElseThrow(() -> new ResourceNotFoundException("The user is not found with id: " + id));
    }

    private boolean passwordMatches(String rawPassword, String storedPassword) {
        // TODO: Replace with proper password hashing
        return storedPassword.equals(rawPassword);
    }
}
