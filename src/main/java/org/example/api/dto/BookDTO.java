package org.example.api.dto;

import lombok.Value;

import java.util.UUID;

@Value
public class BookDTO {
    UUID id;
    String title;
    String image;
}
