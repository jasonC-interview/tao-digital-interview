package org.example.api.mapper;

import org.example.api.dto.*;
import org.example.api.entity.Book;
import org.example.api.entity.Inventory;
import org.example.api.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class LibraryMapperTest {
    @InjectMocks
    private LibraryMapper libraryMapper;

    private UUID bookId;
    private UUID userId;
    private UUID inventoryId;
    private Book book;
    private User user;
    private Inventory inventory;
    private Instant loanDate;

    @BeforeEach
    void setUp() {
        bookId = UUID.randomUUID();
        userId = UUID.randomUUID();
        inventoryId = UUID.randomUUID();
        loanDate = Instant.now();

        book = new Book();
        book.setId(bookId);
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setImage("test.jpg");
        book.setInventories(new ArrayList<>());

        user = new User();
        user.setId(userId);
        user.setUsername("user");
        user.setRole("USER");
        user.setInventories(new ArrayList<>());

        inventory = new Inventory();
        inventory.setId(inventoryId);
        inventory.setBook(book);
        inventory.setUser(user);
        inventory.setLoanDate(loanDate);

        book.getInventories().add(inventory);
        user.getInventories().add(inventory);
    }

    @Nested
    @DisplayName("BookDTO Mapping Tests")
    class BookDtoMappingTests {
        @Test
        void toBookDTO_ShouldMapAllFields() {
            BookDTO result = libraryMapper.toBookDTO(book);

            assertNotNull(result);
            assertEquals(bookId, result.id());
            assertEquals("Test Book", result.title());
            assertEquals("test.jpg", result.image());
        }

        @Test
        void toBookDTO_ShouldReturnNull_WhenBookIsNull() {
            assertNull(libraryMapper.toBookDTO(null));
        }

        @Test
        void toBookDTO_ShouldMapAllFields_WithNullImage() {
            book.setImage(null);

            BookDTO result = libraryMapper.toBookDTO(book);

            assertNotNull(result);
            assertEquals(bookId, result.id());
            assertEquals("Test Book", result.title());
            assertNull(result.image());
        }
    }

    @Nested
    @DisplayName("InventoryDTO Mapping Tests")
    class InventoryDtoMappingTests {
        @Test
        void toInventoryDTO_ShouldMapAllFields() {
            InventoryDTO result = libraryMapper.toInventoryDTO(inventory);

            assertNotNull(result);
            assertEquals(inventoryId, result.id());
            assertEquals(loanDate, result.loanDate());
            assertNotNull(result.book());
            assertEquals(bookId, result.book().id());
            assertEquals("Test Book", result.book().title());
            assertEquals("test.jpg", result.book().image());
        }

        @Test
        void toInventoryDTO_ShouldReturnNull_WhenInventoryIsNull() {
            assertNull(libraryMapper.toInventoryDTO(null));
        }

        @Test
        void toInventoryDTO_ShouldMapAllFields_WithNullLoanDate() {
            inventory.setLoanDate(null);

            InventoryDTO result = libraryMapper.toInventoryDTO(inventory);

            assertNotNull(result);
            assertEquals(inventoryId, result.id());
            assertNull(result.loanDate());
            assertNotNull(result.book());
        }
    }

    @Nested
    @DisplayName("UserDTO Mapping Tests")
    class UserDtoMappingTests {
        @Test
        void toUserDTO_ShouldMapAllFields() {
            List<Inventory> inventories = List.of(inventory);
            user.setInventories(inventories);

            UserDTO result = libraryMapper.toUserDTO(user);

            assertNotNull(result);
            assertEquals(userId, result.id());
            assertEquals("user", result.username());
            assertEquals("USER", result.role());
            assertFalse(result.inventories().isEmpty());
            assertEquals(1, result.inventories().size());

            InventoryDTO firstInventory = result.inventories().getFirst();
            assertEquals(inventoryId, firstInventory.id());
            assertEquals(loanDate, firstInventory.loanDate());
        }

        @Test
        void toUserDTO_ShouldReturnNull_WhenUserIsNull() {
            assertNull(libraryMapper.toUserDTO(null));
        }

        @Test
        void toUserDTO_ShouldMapAllFields_WithEmptyInventories() {
            user.setInventories(Collections.emptyList());

            UserDTO result = libraryMapper.toUserDTO(user);

            assertNotNull(result);
            assertEquals(userId, result.id());
            assertEquals("user", result.username());
            assertEquals("USER", result.role());
            assertTrue(result.inventories().isEmpty());
        }
    }

    @Nested
    @DisplayName("InventoryUserDTO Mapping Tests")
    class InventoryUserDtoMappingTests {
        @Test
        void toInventoryUserDTO_ShouldMapAllFields() {
            InventoryUserDTO result = libraryMapper.toInventoryUserDTO(inventory);

            assertNotNull(result);
            assertEquals(inventoryId, result.id());
            assertEquals(loanDate, result.loanDate());
            assertNotNull(result.user());
            assertEquals(userId, result.user().id());
            assertEquals("user", result.user().username());
            assertEquals("USER", result.user().role());
        }

        @Test
        void toInventoryUserDTO_ShouldReturnNull_WhenInventoryIsNull() {
            assertNull(libraryMapper.toInventoryUserDTO(null));
        }

        @Test
        void toInventoryUserDTO_ShouldMapAllFields_WithNullUser() {
            inventory.setUser(null);

            InventoryUserDTO result = libraryMapper.toInventoryUserDTO(inventory);

            assertNotNull(result);
            assertEquals(inventoryId, result.id());
            assertEquals(loanDate, result.loanDate());
            assertNull(result.user());
        }
    }

    @Nested
    @DisplayName("BookInventoryUserDTO Mapping Tests")
    class BookInventoryUserDtoMappingTests {
        @Test
        void toBookInventoryUserDTO_ShouldMapAllFields() {
            List<Inventory> inventories = List.of(inventory);
            book.setInventories(inventories);

            BookDetailsDTO result = libraryMapper.toBookDetailsDTO(book);

            assertNotNull(result);
            assertEquals(bookId, result.id());
            assertEquals("Test Book", result.title());
            assertEquals("Test Author", result.author());
            assertEquals("test.jpg", result.image());
            assertFalse(result.inventories().isEmpty());
            assertEquals(1, result.inventories().size());

            InventoryUserDTO firstInventory = result.inventories().getFirst();
            assertEquals(inventoryId, firstInventory.id());
            assertEquals(loanDate, firstInventory.loanDate());
            assertNotNull(firstInventory.user());
            assertEquals(userId, firstInventory.user().id());
        }

        @Test
        void toBookDetailsDTO_ShouldReturnNull_WhenBookIsNull() {
            assertNull(libraryMapper.toBookDetailsDTO(null));
        }

        @Test
        void toBookDetailsDTO_ShouldMapAllFields_WithEmptyInventories() {
            book.setInventories(Collections.emptyList());

            BookDetailsDTO result = libraryMapper.toBookDetailsDTO(book);

            assertNotNull(result);
            assertEquals(bookId, result.id());
            assertEquals("Test Book", result.title());
            assertEquals("Test Author", result.author());
            assertEquals("test.jpg", result.image());
            assertTrue(result.inventories().isEmpty());
        }
    }
}