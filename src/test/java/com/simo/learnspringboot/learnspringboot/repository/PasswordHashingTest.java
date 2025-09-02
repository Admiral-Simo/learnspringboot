package com.simo.learnspringboot.learnspringboot.repository;

import com.simo.learnspringboot.learnspringboot.dto.RegisterRequestDto;
import com.simo.learnspringboot.learnspringboot.model.User;
import com.simo.learnspringboot.learnspringboot.service.AuthService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class PasswordHashingTest {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    @Test
    void whenCreateUser_thenPasswordShouldBeHashed() {
        RegisterRequestDto newUser = new RegisterRequestDto("simo", "simo@gmail.com", "Password@123");

        authService.register(newUser);

        Optional<User> user = userRepository.findByEmail("simo@gmail.com");
        assertThat(user).isPresent();

        String storedPassword = user.get().getPassword();

        assertThat(storedPassword)
                .isNotNull()
                .isNotEqualTo("Password@123");

        boolean matches = passwordEncoder.matches(newUser.password(), user.get().getPassword());
        assertThat(matches).isTrue();
    }
}
