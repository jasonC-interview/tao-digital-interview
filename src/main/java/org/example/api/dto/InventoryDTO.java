package org.example.api.dto;

import lombok.Value;

import java.time.LocalDateTime;
import java.util.UUID;

@Value
public class InventoryDTO {
    UUID id;
    LocalDateTime loanDate;
    BookDTO book;
}
