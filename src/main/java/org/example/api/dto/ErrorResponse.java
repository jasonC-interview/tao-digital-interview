package org.example.api.dto;

import java.util.List;

public record ErrorResponse(List<String> errors) {
}
