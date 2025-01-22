package org.example.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.api.dto.BookDetailsDTO;
import org.example.api.dto.BorrowRequest;
import org.example.api.dto.ReturnRequest;
import org.example.api.exception.ConflictException;
import org.example.api.exception.ResourceNotFoundException;
import org.example.api.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
class BookControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID bookId;
    private UUID inventoryId;
    private UUID userId;
    private BookDetailsDTO bookDTO;

    @BeforeEach
    void setUp() {
        bookId = UUID.randomUUID();
        inventoryId = UUID.randomUUID();
        userId = UUID.randomUUID();

        bookDTO = new BookDetailsDTO(
                bookId,
                "Test Book",
                "Test Author",
                "image.jpg",
                List.of()
        );
    }

    @Nested
    @DisplayName("GET /books endpoints")
    class GetBooksTests {
        @Test
        void getAllBooks_ShouldReturnBooks() throws Exception {
            when(bookService.getAllBooks()).thenReturn(List.of(bookDTO));

            mockMvc.perform(get("/books")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].id").value(bookId.toString()))
                    .andExpect(jsonPath("$[0].title").value("Test Book"));

            verify(bookService).getAllBooks();
        }

        @Test
        void getBookById_ShouldReturnBook_WhenExists() throws Exception {
            when(bookService.getBookById(bookId)).thenReturn(bookDTO);

            mockMvc.perform(get("/books/{id}", bookId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(bookId.toString()))
                    .andExpect(jsonPath("$.title").value("Test Book"));

            verify(bookService).getBookById(bookId);
        }

        @Test
        void getBookById_ShouldReturn404_WhenNotFound() throws Exception {
            when(bookService.getBookById(bookId))
                    .thenThrow(new ResourceNotFoundException("Book not found"));

            mockMvc.perform(get("/books/{id}", bookId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());

            verify(bookService).getBookById(bookId);
        }
    }

    @Nested
    @DisplayName("PUT /books/borrow endpoint")
    class BorrowBookTests {
        @Test
        void borrowBook_ShouldReturn202_WhenSuccessful() throws Exception {
            BorrowRequest request = new BorrowRequest(userId, inventoryId);

            mockMvc.perform(put("/books/borrow")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isAccepted());

            verify(bookService).borrowBook(request);
        }

        @Test
        void borrowBook_ShouldReturn409_WhenBookAlreadyBorrowed() throws Exception {
            BorrowRequest request = new BorrowRequest(userId, inventoryId);
            doThrow(new ConflictException("Book already borrowed"))
                    .when(bookService).borrowBook(request);

            mockMvc.perform(put("/books/borrow")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict());

            verify(bookService).borrowBook(request);
        }

        @Test
        void borrowBook_ShouldReturn400_WhenInvalidRequest() throws Exception {
            BorrowRequest request = new BorrowRequest(null, null);

            mockMvc.perform(put("/books/borrow")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(bookService, never()).borrowBook(any());
        }

        @Test
        void borrowBook_ShouldReturn404_WhenInventoryNotFound() throws Exception {
            BorrowRequest request = new BorrowRequest(userId, inventoryId);
            doThrow(new ResourceNotFoundException("Inventory not found"))
                    .when(bookService).borrowBook(request);

            mockMvc.perform(put("/books/borrow")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());

            verify(bookService).borrowBook(request);
        }
    }

    @Nested
    @DisplayName("PUT /books/return endpoint")
    class ReturnBookTests {
        @Test
        void returnBook_ShouldReturn202_WhenSuccessful() throws Exception {
            ReturnRequest request = new ReturnRequest(userId, inventoryId);

            mockMvc.perform(put("/books/return")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isAccepted());

            verify(bookService).returnBook(request);
        }

        @Test
        void returnBook_ShouldReturn409_WhenBookNotBorrowed() throws Exception {
            ReturnRequest request = new ReturnRequest(userId, inventoryId);
            doThrow(new ConflictException("Book is not currently borrowed"))
                    .when(bookService).returnBook(request);

            mockMvc.perform(put("/books/return")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict());

            verify(bookService).returnBook(request);
        }

        @Test
        void returnBook_ShouldReturn409_WhenWrongUser() throws Exception {
            ReturnRequest request = new ReturnRequest(userId, inventoryId);
            doThrow(new ConflictException("Book can't be returned by another user"))
                    .when(bookService).returnBook(request);

            mockMvc.perform(put("/books/return")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict());

            verify(bookService).returnBook(request);
        }

        @Test
        void returnBook_ShouldReturn400_WhenInvalidRequest() throws Exception {
            ReturnRequest request = new ReturnRequest(null, null);

            mockMvc.perform(put("/books/return")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(bookService, never()).returnBook(any());
        }

        @Test
        void returnBook_ShouldReturn404_WhenInventoryNotFound() throws Exception {
            ReturnRequest request = new ReturnRequest(userId, inventoryId);
            doThrow(new ResourceNotFoundException("Inventory not found"))
                    .when(bookService).returnBook(request);

            mockMvc.perform(put("/books/return")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());

            verify(bookService).returnBook(request);
        }
    }
}