package org.example.api.dto;

import java.util.List;
import java.util.UUID;

public record BookDetailsDTO(UUID id, String title, String author, String image, List<InventoryUserDTO> inventories) {
}

