package org.example.api.dto;

import lombok.Value;

import java.util.List;

@Value
public class ErrorResponse {
    List<String> errors;
}
