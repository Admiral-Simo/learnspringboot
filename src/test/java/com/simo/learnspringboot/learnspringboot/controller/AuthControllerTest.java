package com.simo.learnspringboot.learnspringboot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.simo.learnspringboot.learnspringboot.dto.AuthResponseDto;
import com.simo.learnspringboot.learnspringboot.dto.LoginRequestDto;
import com.simo.learnspringboot.learnspringboot.dto.RegisterRequestDto;
import com.simo.learnspringboot.learnspringboot.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void shouldRegisterUserSuccessfully() throws Exception {
        RegisterRequestDto request = new RegisterRequestDto(
                "Alice", "alice@example.com", "password123"
        );

        AuthResponseDto mockResponse = new AuthResponseDto(
                "mockedToken",
                "alice@example.com",
                "ROLE_USER",
                "User registered successfully!"
        );

        when(authService.register(request)).thenReturn(mockResponse);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("alice@example.com"))
                .andExpect(jsonPath("$.role").value("ROLE_USER"))
                .andExpect(jsonPath("$.token").value("mockedToken"))
                .andExpect(jsonPath("$.message").value("User registered successfully!"));

        verify(authService, times(1)).register(request);
    }

    @Test
    void shouldReturnErrorStatusOnRegisterFailure() throws Exception {
        RegisterRequestDto request = new RegisterRequestDto(
                "Alice", "alice@example.com", "password123"
        );

        when(authService.register(request))
                .thenThrow(new RuntimeException("Email already in use!"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Email already in use!"));

        verify(authService, times(1)).register(request);
    }

    @Test
    void shouldLoginUserSuccessfully() throws Exception {
        LoginRequestDto request = new LoginRequestDto(
                "alice@example.com", "password123"
        );

        AuthResponseDto mockResponse = new AuthResponseDto(
                "mockedToken",
                "alice@example.com",
                "ROLE_USER",
                "Logged in successfully!"
        );

        when(authService.login(request)).thenReturn(mockResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("alice@example.com"))
                .andExpect(jsonPath("$.role").value("ROLE_USER"))
                .andExpect(jsonPath("$.token").value("mockedToken"))
                .andExpect(jsonPath("$.message").value("Logged in successfully!"));

        verify(authService, times(1)).login(request);
    }

    @Test
    void shouldReturnErrorStatusOnLoginFailure() throws Exception {
        LoginRequestDto request = new LoginRequestDto(
                "alice@example.com", "wrongpassword"
        );

        when(authService.login(request))
                .thenThrow(new RuntimeException("Invalid credentials"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Invalid credentials"));

        verify(authService, times(1)).login(request);
    }
}
