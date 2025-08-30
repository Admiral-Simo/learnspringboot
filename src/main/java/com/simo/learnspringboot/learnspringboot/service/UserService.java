package com.simo.learnspringboot.learnspringboot.service;

import com.simo.learnspringboot.learnspringboot.model.User;
import com.simo.learnspringboot.learnspringboot.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository repo;

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    public Optional<User> getUserByEmail(String email) {
        return repo.findByEmail(email);
    }
}

