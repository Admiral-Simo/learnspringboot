package com.simo.learnspringboot.learnspringboot.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue
    private UUID id;

    private String passwordResetToken;
    private LocalDateTime tokenExpiryDate;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    private String role; // e.g., ROLE_USER, ROLE_ADMIN
}
