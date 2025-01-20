package org.example.api.dto;

import lombok.Value;

import java.util.List;
import java.util.UUID;

@Value
public class BookInventoryUserDTO {
    UUID id;
    String title;
    String author;
    String image;
    List<InventoryUserDTO> inventories;
}

