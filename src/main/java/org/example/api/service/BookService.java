package org.example.api.service;

import lombok.RequiredArgsConstructor;
import org.example.api.dto.BookInventoryUserDTO;
import org.example.api.dto.BorrowRequest;
import org.example.api.entity.Inventory;
import org.example.api.entity.User;
import org.example.api.exception.ConflictException;
import org.example.api.exception.ResourceNotFoundException;
import org.example.api.mapper.LibraryMapper;
import org.example.api.repository.BookRepository;
import org.example.api.repository.InventoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookService {
    private final LibraryMapper libraryMapper;
    private final BookRepository bookRepository;
    private final InventoryRepository inventoryRepository;
    private final UserService userService;

    @Transactional(readOnly = true)
    public List<BookInventoryUserDTO> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(libraryMapper::toBookInventoryUserDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public BookInventoryUserDTO getBookById(UUID id) {
        return bookRepository.findById(id)
                .map(libraryMapper::toBookInventoryUserDTO)
                .orElseThrow(() -> new ResourceNotFoundException("The book is not found with id: " + id));
    }

    @Transactional
    public void borrowBook(BorrowRequest borrowRequest) {
        UUID inventoryId = borrowRequest.getInventoryId();
        Inventory inventory = inventoryRepository.findByIdWithPessimisticLock(inventoryId)
                .orElseThrow(() -> new ResourceNotFoundException("The inventory is not found with id: " + inventoryId));

        if (inventory.getUser() != null) {
            throw new ConflictException("Book is already borrowed");
        }

        User user = userService.getUserById(borrowRequest.getUserId());

        inventory.setUser(user);
        inventory.setLoanDate(LocalDateTime.now());
        inventoryRepository.save(inventory);
    }
}
