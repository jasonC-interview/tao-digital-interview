package org.example.api.dto;

import lombok.Value;

import java.time.Instant;
import java.util.UUID;

@Value
public class InventoryUserDTO {
    UUID id;
    Instant loanDate;
    UserDTO user;
}
