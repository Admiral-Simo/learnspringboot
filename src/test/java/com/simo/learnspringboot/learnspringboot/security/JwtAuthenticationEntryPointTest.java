package com.simo.learnspringboot.learnspringboot.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.AuthenticationException;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the JwtAuthenticationEntryPoint class.
 * This test verifies that the commence method correctly formats and sends
 * an unauthorized (401) JSON response.
 */
@ExtendWith(MockitoExtension.class)
class JwtAuthenticationEntryPointTest {

    // The class we are testing
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    // Mocks for the arguments of the 'commence' method
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private AuthenticationException authException;
    @Mock
    private ServletOutputStream outputStream;

    // ArgumentCaptor to capture the JSON string written to the response
    @Captor
    private ArgumentCaptor<String> responseBodyCaptor;

    // ObjectMapper to parse the JSON response for assertions
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        // A new instance is created before each test
        jwtAuthenticationEntryPoint = new JwtAuthenticationEntryPoint();
    }

    @Test
    @DisplayName("Should write a 401 Unauthorized JSON response")
    void shouldWriteUnauthorizedJsonResponse() throws Exception {
        // --- Arrange ---
        String expectedPath = "/api/data";
        String expectedErrorMessage = "Authentication token was either missing or invalid.";

        // 1. Configure the mocks' behavior
        when(request.getServletPath()).thenReturn(expectedPath);
        when(authException.getMessage()).thenReturn(expectedErrorMessage);
        when(response.getOutputStream()).thenReturn(outputStream);

        // --- Act ---
        // Call the method under test
        jwtAuthenticationEntryPoint.commence(request, response, authException);

        // --- Assert ---
        // 1. Verify that the response status and content type were set correctly
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response).setContentType("application/json");

        // 2. Capture the string argument that was passed to the output stream's println method
        verify(outputStream).println(responseBodyCaptor.capture());
        String capturedJsonBody = responseBodyCaptor.getValue();

        // 3. Parse the captured JSON string back into a Map for easy assertions
        Map<String, Object> responseBody = objectMapper.readValue(
                capturedJsonBody, new TypeReference<>() {}
        );

        // 4. Assert that the content of the JSON body is correct
        assertThat(responseBody.get("status")).isEqualTo(HttpServletResponse.SC_UNAUTHORIZED);
        assertThat(responseBody.get("error")).isEqualTo("Unauthorized");
        assertThat(responseBody.get("message")).isEqualTo(expectedErrorMessage);
        assertThat(responseBody.get("path")).isEqualTo(expectedPath);
        assertThat(responseBody.get("timestamp")).isNotNull(); // We just care that it exists
    }
}
