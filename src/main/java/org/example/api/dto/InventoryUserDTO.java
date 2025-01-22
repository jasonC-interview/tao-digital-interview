package org.example.api.dto;

import java.time.Instant;
import java.util.UUID;

public record InventoryUserDTO(UUID id, Instant loanDate, UserDTO user) {
}
