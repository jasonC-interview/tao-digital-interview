package org.example.api.dto;

import java.time.Instant;
import java.util.UUID;

public record InventoryDTO(UUID id, Instant loanDate, BookDTO book) {
}
