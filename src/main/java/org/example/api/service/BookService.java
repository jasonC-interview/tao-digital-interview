package org.example.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.api.dto.BookDetailsDTO;
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
import org.springframework.dao.DataAccessException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@EnableRetry
// TODO: Consider adding caching for frequently accessed data
public class BookService {
    private final LibraryMapper libraryMapper;
    private final BookRepository bookRepository;
    private final InventoryRepository inventoryRepository;
    private final UserService userService;

    @Transactional(readOnly = true)
    public List<BookDetailsDTO> getAllBooks() {
        log.debug("Fetching all books from database");
        List<Book> books = bookRepository.findAll();
        log.debug("Found {} books", books.size());

        return books.stream()
                .map(libraryMapper::toBookDetailsDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public BookDetailsDTO getBookById(UUID id) {
        log.debug("Fetching book with id: {}", id);
        return bookRepository.findById(id)
                .map(libraryMapper::toBookDetailsDTO)
                .orElseThrow(() -> new ResourceNotFoundException("The book is not found with id: " + id));
    }

    @Retryable(
            retryFor = {PessimisticLockingFailureException.class, DataAccessException.class},
            maxAttempts = 3,
            backoff = @Backoff(
                    delay = 1000,
                    maxDelay = 4000,
                    multiplier = 2,
                    random = true
            )
    )
    @Transactional
    public void borrowBook(BorrowRequest borrowRequest) {
        UUID inventoryId = borrowRequest.inventoryId();
        UUID userId = borrowRequest.userId();
        log.debug("Processing borrow request - userId: {}, inventoryId: {}", userId, inventoryId);
        if (RetrySynchronizationManager.getContext() != null) {
            log.debug("Attempting to borrow book. Attempt number: {}",
                    RetrySynchronizationManager.getContext().getRetryCount() + 1);
        }

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
        log.info("Book borrowed successfully - userId: {}, inventoryId: {}", userId, inventoryId);
    }

    @Retryable(
            retryFor = {PessimisticLockingFailureException.class, DataAccessException.class},
            maxAttempts = 3,
            backoff = @Backoff(
                    delay = 1000,
                    maxDelay = 4000,
                    multiplier = 2,
                    random = true
            )
    )
    @Transactional
    public void returnBook(ReturnRequest request) {
        UUID inventoryId = request.inventoryId();
        UUID userId = request.userId();
        log.debug("Processing return request - userId: {}, inventoryId: {}", userId, inventoryId);
        if (RetrySynchronizationManager.getContext() != null) {
            log.debug("Attempting to return book. Attempt number: {}",
                    RetrySynchronizationManager.getContext().getRetryCount() + 1);
        }

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
        log.info("Book returned successfully - userId: {}, inventoryId: {}", userId, inventoryId);
    }

    @Recover
    public void recoverBorrowOperation(Exception e, BorrowRequest request) throws Exception {
        if (e instanceof ConflictException || e instanceof ResourceNotFoundException) {
            throw e;
        }

        log.error("Failed to complete borrow operation after retries. InventoryId: {}, UserId: {}",
                request.inventoryId(), request.userId(), e);
        throw new ConflictException("Unable to complete borrow operation. Please try again later.");
    }

    @Recover
    public void recoverReturnOperation(Exception e, ReturnRequest request) throws Exception {
        if (e instanceof ConflictException || e instanceof ResourceNotFoundException) {
            throw e;
        }

        log.error("Failed to complete return operation after retries. InventoryId: {}, UserId: {}",
                request.inventoryId(), request.userId(), e);
        throw new ConflictException("Unable to complete return operation. Please try again later.");
    }
}
