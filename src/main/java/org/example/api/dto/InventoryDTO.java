package org.example.api.dto;

import lombok.Value;

import java.time.Instant;
import java.util.UUID;

@Value
public class InventoryDTO {
    UUID id;
    Instant loanDate;
    BookDTO book;
}
