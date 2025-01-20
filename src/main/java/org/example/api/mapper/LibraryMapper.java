package org.example.api.mapper;

import org.example.api.dto.*;
import org.example.api.entity.AppUser;
import org.example.api.entity.Book;
import org.example.api.entity.Inventory;
import org.springframework.stereotype.Component;

@Component
public class LibraryMapper {

    public BookDTO toBookDTO(Book book) {
        if (book == null) {
            return null;
        }

        return new BookDTO(book.getId(), book.getTitle(), book.getImage());
    }

    public InventoryDTO toInventoryDTO(Inventory inventory) {
        if (inventory == null) {
            return null;
        }

        return new InventoryDTO(
                inventory.getId(),
                inventory.getLoanDate(),
                toBookDTO(inventory.getBook())
        );
    }

    public UserDTO toUserDTO(AppUser user) {
        if (user == null) {
            return null;
        }

        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getRole(),
                user.getInventories().stream()
                        .map(this::toInventoryDTO)
                        .toList()
        );
    }

    public InventoryUserDTO toInventoryUserDTO(Inventory inventory) {
        if (inventory == null) {
            return null;
        }

        return new InventoryUserDTO(
                inventory.getId(),
                inventory.getLoanDate(),
                toUserDTO(inventory.getUser())
        );
    }

    public BookInventoryUserDTO toBookInventoryUserDTO(Book book) {
        if (book == null) {
            return null;
        }

        return new BookInventoryUserDTO(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getImage(),
                book.getInventories().stream()
                        .map(this::toInventoryUserDTO)
                        .toList()
        );
    }
}
