package org.example.api.controller;

import lombok.RequiredArgsConstructor;
import org.example.api.dto.BookInventoryUserDTO;
import org.example.api.service.BookService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @GetMapping
    public List<BookInventoryUserDTO> getAllBooks() {
        return bookService.getAllBooks();
    }
}
