package com.simo.learnspringboot.learnspringboot.repository;

import com.simo.learnspringboot.learnspringboot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    Optional<User> findByPasswordResetToken(String token);
    Optional<User> findByEmailVerificationToken(String token);
}