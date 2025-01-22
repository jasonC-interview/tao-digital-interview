package org.example.api.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ReturnRequest(@NotNull(message = "User ID is required") UUID userId,
                            @NotNull(message = "Inventory ID is required") UUID inventoryId) {
}
