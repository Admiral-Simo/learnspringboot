package com.simo.learnspringboot.learnspringboot.security;

import com.simo.learnspringboot.learnspringboot.model.User;
import com.simo.learnspringboot.learnspringboot.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTest {
    @Mock UserRepository userRepository;

    @InjectMocks CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("Should return UserDetails when user is found by email")
    void loadUserByUsername_UserFound_ReturnsUserDetails() {
        // preparation
        User user = new User();
        user.setEmail("simo@gmail.com");
        user.setPassword("password");
        user.setRole("ROLE_ADMIN");
        user.setVerified(true);

        // expect
        when(userRepository.findByEmail("simo@gmail.com"))
                .thenReturn(Optional.of(user));

        // action
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("simo@gmail.com");

        // assertion
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority("ROLE_ADMIN");
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(user.getEmail());
        assertThat(userDetails.getPassword()).isEqualTo(user.getPassword());
        assertThat(userDetails.getAuthorities()).hasSize(1);
        assertThat(userDetails.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly(user.getRole());

        // verification
        verify(userRepository).findByEmail(user.getEmail());
    }

    @Test
    @DisplayName("Should return UserDetails when user is found by email")
    void loadUserByUsername_UserNotFound_ThrowUsernameNotFoundException() {
        String email = "simo@gmail.com";
        when(userRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        // action
        UsernameNotFoundException thrown = assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername(email);
        });

        // assertion
        String expectedMessage = "User not found: " + email;
        assertThat(thrown).isNotNull();
        assertThat(thrown.getMessage()).isEqualTo(expectedMessage);

        // verification
        verify(userRepository).findByEmail(email);
    }

    // Add this new test method
    @Test
    @DisplayName("Should throw DisabledException if user is not verified")
    void loadUserByUsername_UserNotVerified_ThrowsDisabledException() {
        // Arrange
        String email = "unverified@gmail.com";
        User unverifiedUser = new User();
        unverifiedUser.setEmail(email);
        unverifiedUser.setPassword("password");
        unverifiedUser.setRole("ROLE_USER");
        unverifiedUser.setVerified(false); // Explicitly unverified

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(unverifiedUser));

        // Act & Assert
        DisabledException thrown = assertThrows(DisabledException.class, () -> {
            customUserDetailsService.loadUserByUsername(email);
        });

        assertThat(thrown.getMessage()).isEqualTo("User account is not verified. Please check your email.");
    }}
