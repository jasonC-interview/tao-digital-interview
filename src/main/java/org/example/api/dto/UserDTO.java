package org.example.api.dto;

import java.util.List;
import java.util.UUID;

public record UserDTO(UUID id, String username, String role, List<InventoryDTO> inventories) {
}
