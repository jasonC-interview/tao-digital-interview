package org.example.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.api.dto.BookDetailsDTO;
import org.example.api.dto.BorrowRequest;
import org.example.api.dto.ReturnRequest;
import org.example.api.service.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
@Slf4j
public class BookController {
    // TODO: Implement rate limiting
    private final BookService bookService;

    @GetMapping
    public List<BookDetailsDTO> getAllBooks() {
        log.info("Retrieving all books");
        return bookService.getAllBooks();
    }

    @GetMapping("/{id}")
    public BookDetailsDTO getBookById(@PathVariable UUID id) {
        log.info("Retrieving book with id: {}", id);
        return bookService.getBookById(id);
    }

    @PutMapping("/borrow")
    public ResponseEntity<Void> borrowBook(@Valid @RequestBody BorrowRequest request) {
        log.info("Processing borrow request - userId: {}, inventoryId: {}",
                request.userId(), request.inventoryId());
        bookService.borrowBook(request);
        return ResponseEntity.accepted().build();
    }

    @PutMapping("/return")
    public ResponseEntity<Void> returnBook(@Valid @RequestBody ReturnRequest request) {
        log.info("Processing return request - userId: {}, inventoryId: {}",
                request.userId(), request.inventoryId());
        bookService.returnBook(request);
        return ResponseEntity.accepted().build();
    }
}
