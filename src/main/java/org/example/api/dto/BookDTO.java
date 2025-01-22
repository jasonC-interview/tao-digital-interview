package org.example.api.dto;

import java.util.UUID;

public record BookDTO(UUID id, String title, String image) {
}
