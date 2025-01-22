package org.example.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.api.dto.BookInventoryUserDTO;
import org.example.api.dto.BorrowRequest;
import org.example.api.dto.ReturnRequest;
import org.example.api.entity.Book;
import org.example.api.entity.Inventory;
import org.example.api.entity.User;
import org.example.api.exception.ConflictException;
import org.example.api.exception.ResourceNotFoundException;
import org.example.api.mapper.LibraryMapper;
import org.example.api.repository.BookRepository;
import org.example.api.repository.InventoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookService {
    private final LibraryMapper libraryMapper;
    private final BookRepository bookRepository;
    private final InventoryRepository inventoryRepository;
    private final UserService userService;

    @Transactional(readOnly = true)
    public List<BookInventoryUserDTO> getAllBooks() {
        log.debug("Fetching all books from database");
        List<Book> books = bookRepository.findAll();
        log.debug("Found {} books", books.size());

        return books.stream()
                .map(libraryMapper::toBookInventoryUserDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public BookInventoryUserDTO getBookById(UUID id) {
        log.debug("Fetching book with id: {}", id);
        return bookRepository.findById(id)
                .map(libraryMapper::toBookInventoryUserDTO)
                .orElseThrow(() -> new ResourceNotFoundException("The book is not found with id: " + id));
    }

    @Transactional
    public void borrowBook(BorrowRequest borrowRequest) {
        UUID inventoryId = borrowRequest.getInventoryId();
        UUID userId = borrowRequest.getUserId();
        log.debug("Processing borrow request - inventoryId: {}, userId: {}", inventoryId, userId);

        Inventory inventory = inventoryRepository.findByIdWithPessimisticLock(inventoryId)
                .orElseThrow(() -> {
                    log.warn("Inventory not found with id: {}", inventoryId);
                    return new ResourceNotFoundException("The inventory is not found with id: " + inventoryId);
                });

        if (inventory.getUser() != null) {
            log.warn("Attempted to borrow already borrowed book - inventoryId: {}", inventoryId);
            throw new ConflictException("Book is already borrowed");
        }

        User user = userService.getUserById(userId);

        inventory.setUser(user);
        inventory.setLoanDate(Instant.now());
        inventoryRepository.save(inventory);
        log.info("Book borrowed successfully - inventoryId: {}, userId: {}", inventoryId, userId);
    }

    @Transactional
    public void returnBook(ReturnRequest request) {
        UUID inventoryId = request.getInventoryId();
        UUID userId = request.getUserId();
        log.debug("Processing return request - inventoryId: {}, userId: {}", inventoryId, userId);

        Inventory inventory = inventoryRepository.findByIdWithPessimisticLock(inventoryId)
                .orElseThrow(() -> {
                    log.warn("Inventory not found with id: {}", inventoryId);
                    return new ResourceNotFoundException("The inventory is not found with id: " + inventoryId);
                });

        if (inventory.getUser() == null) {
            log.warn("Attempted to return non-borrowed book - inventoryId: {}", inventoryId);
            throw new ConflictException("Book is not currently borrowed");
        }

        UUID actualUserId = inventory.getUser().getId();
        if (!actualUserId.equals(userId)) {
            log.warn("Unauthorized return attempt - inventoryId: {}, userId: {}, actualUserId: {}",
                    inventoryId, userId, actualUserId);
            throw new ConflictException("Book can't be returned by another user");
        }

        inventory.setUser(null);
        inventory.setLoanDate(null);
        inventoryRepository.save(inventory);
        log.info("Book returned successfully - inventoryId: {}", inventoryId);
    }
}
