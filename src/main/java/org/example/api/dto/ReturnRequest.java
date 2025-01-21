package org.example.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.util.UUID;

@Value
public class ReturnRequest {
    @NotNull(message = "User ID is required")
    UUID userId;

    @NotNull(message = "Inventory ID is required")
    UUID inventoryId;
}
