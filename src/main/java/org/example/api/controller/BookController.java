package org.example.api.controller;

import lombok.RequiredArgsConstructor;
import org.example.api.dto.BookInventoryUserDTO;
import org.example.api.service.BookService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @GetMapping
    public List<BookInventoryUserDTO> getAllBooks() {
        return bookService.getAllBooks();
    }

    @GetMapping("/{id}")
    public BookInventoryUserDTO getBookById(@PathVariable UUID id) {
        return bookService.getBookById(id);
    }
}
