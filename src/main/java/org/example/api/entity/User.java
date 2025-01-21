package org.example.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "app_user", indexes = @Index(name = "idx_username", columnList = "username"))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    private UUID id;

    private String username;
    private String password;
    private String role;

    @OneToMany(mappedBy = "user")
    private List<Inventory> inventories;
}
