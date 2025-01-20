package org.example.api.dto;

import lombok.Value;

import java.util.List;
import java.util.UUID;

@Value
public class UserDTO {
    UUID id;
    String username;
    String role;
    List<InventoryDTO> inventories;
}
