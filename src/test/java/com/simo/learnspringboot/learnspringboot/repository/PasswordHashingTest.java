package com.simo.learnspringboot.learnspringboot.repository;

import com.simo.learnspringboot.learnspringboot.dto.RegisterRequestDto;
import com.simo.learnspringboot.learnspringboot.model.User;
import com.simo.learnspringboot.learnspringboot.service.AuthService;
import com.simo.learnspringboot.learnspringboot.service.EmailService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

import org.mockito.Mockito; // Import Mockito
import org.springframework.boot.test.context.TestConfiguration; // Import TestConfiguration
import org.springframework.context.annotation.Bean; // Import Bean
import org.springframework.context.annotation.Primary; // Import Primary

@SpringBootTest
@Transactional
public class PasswordHashingTest {

    // Define a nested configuration class specifically for this test
    @TestConfiguration
    static class TestConfig {

        // This method creates a mock EmailService and defines it as a Spring Bean.
        // @Primary ensures this bean is used over the real one if there's a conflict.
        @Bean
        @Primary
        public EmailService emailService() {
            return Mockito.mock(EmailService.class);
        }
    }

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    // We no longer need @MockBean or any EmailService field here.
    // Spring will automatically inject the mock from our TestConfig into the AuthService.

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