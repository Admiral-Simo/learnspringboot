package com.simo.learnspringboot.learnspringboot.service;

import com.simo.learnspringboot.learnspringboot.model.User;
import com.simo.learnspringboot.learnspringboot.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repo;

    @InjectMocks
    private UserService service;

    @Test
    void shouldReturnUserByEmail() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setName("Alice");
        user.setEmail("alice@example.com");

        when(repo.findByEmail("alice@example.com"))
                .thenReturn(Optional.of(user));

        Optional<User> found = service.getUserByEmail("alice@example.com");

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Alice");

        verify(repo).findByEmail("alice@example.com");
    }
}
