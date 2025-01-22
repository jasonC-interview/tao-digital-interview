package org.example.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.api.dto.LoginRequest;
import org.example.api.dto.UserDTO;
import org.example.api.service.UserFacadeService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    // TODO: Implement rate limiting
    private final UserFacadeService userFacadeService;

    @PostMapping("/login")
    public UserDTO login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Processing login request for user: {}", loginRequest.getUsername());
        return userFacadeService.login(loginRequest);
    }

    @GetMapping("/{id}")
    public UserDTO getUserById(@PathVariable UUID id) {
        log.info("Retrieving user with id: {}", id);
        return userFacadeService.getUserById(id);
    }
}
