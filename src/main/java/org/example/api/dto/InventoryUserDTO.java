package org.example.api.dto;

import lombok.Value;

import java.time.LocalDateTime;
import java.util.UUID;

@Value
public class InventoryUserDTO {
    UUID id;
    LocalDateTime loanDate;
    UserDTO user;
}
