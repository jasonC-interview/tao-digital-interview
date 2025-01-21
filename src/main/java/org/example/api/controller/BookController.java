package org.example.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.api.dto.BookInventoryUserDTO;
import org.example.api.dto.BorrowRequest;
import org.example.api.service.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping("/borrow")
    public ResponseEntity<Void> borrowBook(@Valid @RequestBody BorrowRequest request) {
        bookService.borrowBook(request);
        return ResponseEntity.accepted().build();
    }
}
