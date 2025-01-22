package org.example.api.service;

import org.example.api.dto.BookDetailsDTO;
import org.example.api.dto.BorrowRequest;
import org.example.api.dto.ReturnRequest;
import org.example.api.entity.Book;
import org.example.api.entity.Inventory;
import org.example.api.entity.User;
import org.example.api.exception.ConflictException;
import org.example.api.exception.ResourceNotFoundException;
import org.example.api.mapper.LibraryMapper;
import org.example.api.repository.InventoryRepository;
import org.example.api.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.retry.annotation.EnableRetry;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(
        properties = {
                "spring.liquibase.enabled=false",
                "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration"
        },
        classes = BookService.class
)
@EnableRetry
class BookServiceTest {
    @MockBean
    private InventoryRepository inventoryRepository;

    @MockBean
    private UserService userService;

    @MockBean
    private LibraryMapper libraryMapper;

    @MockBean
    private BookRepository bookRepository;

    @Autowired
    private BookService bookService;

    private UUID bookId;
    private UUID inventoryId;
    private UUID userId;
    private Book book;
    private User user;
    private Inventory inventory;

    @BeforeEach
    void setUp() {
        bookId = UUID.randomUUID();
        inventoryId = UUID.randomUUID();
        userId = UUID.randomUUID();

        book = new Book();
        book.setId(bookId);
        book.setTitle("Test Book");
        book.setAuthor("Test Author");

        user = new User();
        user.setId(userId);
        user.setUsername("testuser");

        inventory = new Inventory();
        inventory.setId(inventoryId);
        inventory.setBook(book);
    }

    @Nested
    @DisplayName("Get Books Tests")
    class GetBooksTests {
        @Test
        void getAllBooks_ShouldReturnAllBooks() {
            List<Book> books = List.of(book);
            BookDetailsDTO dto = new BookDetailsDTO(bookId, "Test Book", "Test Author", "image.jpg", List.of());

            when(bookRepository.findAll()).thenReturn(books);
            when(libraryMapper.toBookDetailsDTO(book)).thenReturn(dto);

            List<BookDetailsDTO> result = bookService.getAllBooks();

            assertFalse(result.isEmpty());
            assertEquals(1, result.size());
            assertEquals(bookId, result.getFirst().id());
            verify(bookRepository).findAll();
            verify(libraryMapper).toBookDetailsDTO(book);
        }

        @Test
        void getBookById_ShouldReturnBook_WhenExists() {
            BookDetailsDTO dto = new BookDetailsDTO(bookId, "Test Book", "Test Author", "image.jpg", List.of());

            when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
            when(libraryMapper.toBookDetailsDTO(book)).thenReturn(dto);

            BookDetailsDTO result = bookService.getBookById(bookId);

            assertNotNull(result);
            assertEquals(bookId, result.id());
            verify(bookRepository).findById(bookId);
            verify(libraryMapper).toBookDetailsDTO(book);
        }

        @Test
        void getBookById_ShouldThrowException_WhenNotFound() {
            when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> bookService.getBookById(bookId));
            verify(bookRepository).findById(bookId);
            verify(libraryMapper, never()).toBookDetailsDTO(any());
        }
    }

    @Nested
    @DisplayName("Borrow Book Tests")
    class BorrowBookTests {
        @Test
        void borrowBook_ShouldSucceed_WhenBookAvailable() {
            BorrowRequest request = new BorrowRequest(userId, inventoryId);

            when(inventoryRepository.findByIdWithPessimisticLock(inventoryId))
                    .thenReturn(Optional.of(inventory));
            when(userService.getUserById(userId)).thenReturn(user);

            bookService.borrowBook(request);

            ArgumentCaptor<Inventory> inventoryCaptor = ArgumentCaptor.forClass(Inventory.class);
            verify(inventoryRepository).save(inventoryCaptor.capture());
            Inventory savedInventory = inventoryCaptor.getValue();

            assertEquals(user, savedInventory.getUser());
            assertNotNull(savedInventory.getLoanDate());
        }

        @Test
        void borrowBook_ShouldThrowException_WhenBookAlreadyBorrowed() {
            BorrowRequest request = new BorrowRequest(userId, inventoryId);
            inventory.setUser(new User());

            when(inventoryRepository.findByIdWithPessimisticLock(inventoryId))
                    .thenReturn(Optional.of(inventory));

            assertThrows(ConflictException.class, () -> bookService.borrowBook(request));
            verify(inventoryRepository, never()).save(any());
        }

        @Test
        void borrowBook_ShouldThrowException_WhenInventoryNotFound() {
            BorrowRequest request = new BorrowRequest(userId, inventoryId);

            when(inventoryRepository.findByIdWithPessimisticLock(inventoryId))
                    .thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> bookService.borrowBook(request));
            verify(inventoryRepository, never()).save(any());
            verify(userService, never()).getUserById(any());
        }

        @Test
        void borrowBook_ShouldRetryAndSucceed() {
            BorrowRequest request = new BorrowRequest(userId, inventoryId);

            when(inventoryRepository.findByIdWithPessimisticLock(inventoryId))
                    .thenThrow(new PessimisticLockingFailureException("Lock failed", new SQLException()))
                    .thenThrow(new PessimisticLockingFailureException("Lock failed", new SQLException()))
                    .thenReturn(Optional.of(inventory));
            when(userService.getUserById(userId)).thenReturn(user);

            bookService.borrowBook(request);

            verify(inventoryRepository, times(3)).findByIdWithPessimisticLock(inventoryId);

            ArgumentCaptor<Inventory> inventoryCaptor = ArgumentCaptor.forClass(Inventory.class);
            verify(inventoryRepository).save(inventoryCaptor.capture());
            Inventory savedInventory = inventoryCaptor.getValue();

            assertEquals(user, savedInventory.getUser());
            assertNotNull(savedInventory.getLoanDate());
        }

        @Test
        void borrowBook_ShouldFailAfterMaxRetries() {
            BorrowRequest request = new BorrowRequest(userId, inventoryId);

            when(inventoryRepository.findByIdWithPessimisticLock(inventoryId))
                    .thenThrow(new PessimisticLockingFailureException("Lock failed", new SQLException()));

            assertThrows(ConflictException.class, () -> bookService.borrowBook(request));
            verify(inventoryRepository, times(3)).findByIdWithPessimisticLock(inventoryId);
        }
    }

    @Nested
    @DisplayName("Return Book Tests")
    class ReturnBookTests {
        @Test
        void returnBook_ShouldSucceed_WhenBorrowedByUser() {
            ReturnRequest request = new ReturnRequest(userId, inventoryId);
            inventory.setUser(user);

            when(inventoryRepository.findByIdWithPessimisticLock(inventoryId))
                    .thenReturn(Optional.of(inventory));

            bookService.returnBook(request);

            ArgumentCaptor<Inventory> inventoryCaptor = ArgumentCaptor.forClass(Inventory.class);
            verify(inventoryRepository).save(inventoryCaptor.capture());
            Inventory savedInventory = inventoryCaptor.getValue();

            assertNull(savedInventory.getUser());
            assertNull(savedInventory.getLoanDate());
        }

        @Test
        void returnBook_ShouldThrowException_WhenNotBorrowed() {
            ReturnRequest request = new ReturnRequest(userId, inventoryId);

            when(inventoryRepository.findByIdWithPessimisticLock(inventoryId))
                    .thenReturn(Optional.of(inventory));

            assertThrows(ConflictException.class, () -> bookService.returnBook(request));
            verify(inventoryRepository, never()).save(any());
        }

        @Test
        void returnBook_ShouldThrowException_WhenBorrowedByDifferentUser() {
            ReturnRequest request = new ReturnRequest(userId, inventoryId);
            User differentUser = new User();
            differentUser.setId(UUID.randomUUID());
            inventory.setUser(differentUser);

            when(inventoryRepository.findByIdWithPessimisticLock(inventoryId))
                    .thenReturn(Optional.of(inventory));

            assertThrows(ConflictException.class, () -> bookService.returnBook(request));
            verify(inventoryRepository, never()).save(any());
        }

        @Test
        void returnBook_ShouldRetryAndSucceed() {
            ReturnRequest request = new ReturnRequest(userId, inventoryId);
            inventory.setUser(user);

            when(inventoryRepository.findByIdWithPessimisticLock(inventoryId))
                    .thenThrow(new PessimisticLockingFailureException("Lock failed", new SQLException()))
                    .thenThrow(new PessimisticLockingFailureException("Lock failed", new SQLException()))
                    .thenReturn(Optional.of(inventory));

            bookService.returnBook(request);

            verify(inventoryRepository, times(3)).findByIdWithPessimisticLock(inventoryId);
            ArgumentCaptor<Inventory> inventoryCaptor = ArgumentCaptor.forClass(Inventory.class);
            verify(inventoryRepository).save(inventoryCaptor.capture());

            Inventory savedInventory = inventoryCaptor.getValue();
            assertNull(savedInventory.getUser());
            assertNull(savedInventory.getLoanDate());
        }

        @Test
        void returnBook_ShouldFailAfterMaxRetries() {
            ReturnRequest request = new ReturnRequest(userId, inventoryId);

            when(inventoryRepository.findByIdWithPessimisticLock(inventoryId))
                    .thenThrow(new PessimisticLockingFailureException("Lock failed", new SQLException()));

            assertThrows(ConflictException.class, () -> bookService.returnBook(request));
            verify(inventoryRepository, times(3)).findByIdWithPessimisticLock(inventoryId);
        }
    }
}