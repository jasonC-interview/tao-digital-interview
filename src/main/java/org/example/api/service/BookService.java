package org.example.api.service;

import lombok.RequiredArgsConstructor;
import org.example.api.dto.BookInventoryUserDTO;
import org.example.api.mapper.LibraryMapper;
import org.example.api.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookService {
    private final LibraryMapper libraryMapper;
    private final BookRepository bookRepository;

    @Transactional(readOnly = true)
    public List<BookInventoryUserDTO> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(libraryMapper::toBookInventoryUserDTO)
                .toList();
    }
}
