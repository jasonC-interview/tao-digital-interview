package org.example.api.service;

import lombok.RequiredArgsConstructor;
import org.example.api.dto.LoginRequest;
import org.example.api.dto.UserDTO;
import org.example.api.entity.User;
import org.example.api.mapper.LibraryMapper;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserFacadeService {
    private final LibraryMapper libraryMapper;
    private final UserService userService;

    public UserDTO login(LoginRequest loginRequest) {
        User user = userService.validateCredentials(
                loginRequest.username(),
                loginRequest.password()
        );

        return libraryMapper.toUserDTO(user);
    }

    public UserDTO getUserById(UUID id) {
        User user = userService.getUserById(id);

        return libraryMapper.toUserDTO(user);
    }
}
