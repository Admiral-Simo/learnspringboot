package com.simo.learnspringboot.learnspringboot.repository;

import com.simo.learnspringboot.learnspringboot.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveAndFindByEmail() {
        // given
        User user = new User();
        user.setName("Alice");
        user.setEmail("alice@example.com");
        user.setPassword("secret");
        user.setRole("ROLE_USER");

        userRepository.save(user);

        // when
        Optional<User> found = userRepository.findByEmail("alice@example.com");

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Alice");
    }
}
