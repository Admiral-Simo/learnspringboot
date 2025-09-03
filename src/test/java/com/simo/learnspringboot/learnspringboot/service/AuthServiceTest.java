package com.simo.learnspringboot.learnspringboot.service;

import com.simo.learnspringboot.learnspringboot.dto.AuthResponseDto;
import com.simo.learnspringboot.learnspringboot.dto.LoginRequestDto;
import com.simo.learnspringboot.learnspringboot.dto.RegisterRequestDto;
import com.simo.learnspringboot.learnspringboot.model.User;
import com.simo.learnspringboot.learnspringboot.repository.UserRepository;
import com.simo.learnspringboot.learnspringboot.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @Test
    void shouldRegisterNewUserSuccessfullyAndAsignAdminToSimoEmail() {
        RegisterRequestDto request = new RegisterRequestDto(
                "Simo",
                "simo@gmail.com",
                "Password@123"
        );

        when(userRepository.findByEmail("simo@gmail.com"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode("Password@123"))
                .thenReturn("encodedPassword");

        when(jwtUtil.generateToken("simo@gmail.com"))
                .thenReturn("mockedToken");

        AuthResponseDto responseDto = authService.register(request);

        assertThat(responseDto).isNotNull();
        assertThat(responseDto.email()).isNotNull();
        assertThat(responseDto.token()).isEqualTo("mockedToken");
        assertThat(responseDto.role()).isEqualTo("ROLE_ADMIN");
        assertThat(responseDto.message()).isEqualTo("User registered successfully!");

        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldRegisterNewUserSuccessfullyAndAssignUserToGeneralEmail() {
        RegisterRequestDto request = new RegisterRequestDto(
                "toufik",
                "toufik@gmail.com",
                "Password@123"
        );

        when(userRepository.findByEmail("toufik@gmail.com"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode("Password@123"))
                .thenReturn("encodedPassword");

        when(jwtUtil.generateToken("toufik@gmail.com"))
                .thenReturn("mockedToken");

        AuthResponseDto responseDto = authService.register(request);

        assertThat(responseDto).isNotNull();
        assertThat(responseDto.email()).isNotNull();
        assertThat(responseDto.token()).isEqualTo("mockedToken");
        assertThat(responseDto.role()).isEqualTo("ROLE_USER");
        assertThat(responseDto.message()).isEqualTo("User registered successfully!");

        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionIfEmailAlreadyUsed() {
        RegisterRequestDto request = new RegisterRequestDto(
                "Simo",
                "simo@gmail.com",
                "Password@123"
        );

        User existingUser = new User();
        existingUser.setEmail("simo@gmail.com");
        when(userRepository.findByEmail("simo@gmail.com"))
                .thenReturn(Optional.of(existingUser));

        try {
            authService.register(request);
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).isEqualTo("Email already in use!");
        }

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldLoginSuccessfully() {
        Authentication mockAuth = mock(Authentication.class);

        LoginRequestDto request = new LoginRequestDto("alice@example.com", "password123");

        User user = new User();
        user.setEmail("alice@example.com");
        user.setRole("ROLE_USER");

        // Mock authentication manager (does nothing in unit test)
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuth);

        when(userRepository.findByEmail("alice@example.com"))
                .thenReturn(Optional.of(user));

        when(jwtUtil.generateToken("alice@example.com"))
                .thenReturn("mockedToken");

        AuthResponseDto response = authService.login(request);

        assertThat(response).isNotNull();
        assertThat(response.email()).isEqualTo("alice@example.com");
        assertThat(response.role()).isEqualTo("ROLE_USER");
        assertThat(response.token()).isEqualTo("mockedToken");

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
}
