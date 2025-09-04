package com.simo.learnspringboot.learnspringboot.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.security.Key;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for the JwtUtil class.
 * This class tests token generation, username extraction, and validation logic.
 */
class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        // A new instance is created before each test to ensure isolation.
        jwtUtil = new JwtUtil();
    }

    @Nested
    @DisplayName("Token Generation")
    class GenerateTokenTests {
        @Test
        @DisplayName("Should generate a non-empty, valid token for a given username")
        void shouldGenerateValidToken() {
            // Arrange
            String username = "user@example.com";

            // Act
            String token = jwtUtil.generateToken(username);

            // Assert
            assertThat(token).isNotNull().isNotEmpty();

            // Verify the token is self-consistent
            assertThat(jwtUtil.validateToken(token)).isTrue();
            assertThat(jwtUtil.extractUsername(token)).isEqualTo(username);
        }
    }

    @Nested
    @DisplayName("Username Extraction")
    class ExtractUsernameTests {
        @Test
        @DisplayName("Should extract the correct username from a valid token")
        void shouldExtractUsernameFromValidToken() {
            // Arrange
            String expectedUsername = "testuser";
            String token = jwtUtil.generateToken(expectedUsername);

            // Act
            String actualUsername = jwtUtil.extractUsername(token);

            // Assert
            assertThat(actualUsername).isEqualTo(expectedUsername);
        }

        @Test
        @DisplayName("Should throw ExpiredJwtException for an expired token")
        void shouldThrowExceptionWhenExtractingFromExpiredToken() {
            // Arrange
            String expiredToken = generateTokenWithCustomExpiration("user", -1000); // Expired 1 second ago

            // Act & Assert
            assertThrows(ExpiredJwtException.class, () -> {
                jwtUtil.extractUsername(expiredToken);
            });
        }

        @Test
        @DisplayName("Should throw SignatureException for a token with an invalid signature")
        void shouldThrowExceptionForInvalidSignature() {
            // Arrange
            String tokenWithWrongSignature = generateTokenWithDifferentSecret("user");

            // Act & Assert
            assertThrows(SignatureException.class, () -> {
                jwtUtil.extractUsername(tokenWithWrongSignature);
            });
        }
    }

    @Nested
    @DisplayName("Token Validation")
    class ValidateTokenTests {
        @Test
        @DisplayName("Should return true for a valid, non-expired token")
        void shouldReturnTrueForValidToken() {
            // Arrange
            String token = jwtUtil.generateToken("validuser");

            // Act
            boolean isValid = jwtUtil.validateToken(token);

            // Assert
            assertThat(isValid).isTrue();
        }

        @Test
        @DisplayName("Should return false for an expired token")
        void shouldReturnFalseForExpiredToken() {
            // Arrange
            String expiredToken = generateTokenWithCustomExpiration("user", -5000); // Expired 5 seconds ago

            // Act
            boolean isValid = jwtUtil.validateToken(expiredToken);

            // Assert
            assertThat(isValid).isFalse();
        }

        @Test
        @DisplayName("Should return false for a token with a different signature")
        void shouldReturnFalseForTokenWithInvalidSignature() {
            // Arrange
            String invalidToken = generateTokenWithDifferentSecret("user");

            // Act
            boolean isValid = jwtUtil.validateToken(invalidToken);

            // Assert
            assertThat(isValid).isFalse();
        }

        @Test
        @DisplayName("Should return false for a malformed or invalid token string")
        void shouldReturnFalseForMalformedToken() {
            // Act
            boolean isValid = jwtUtil.validateToken("not.a.jwt.token");

            // Assert
            assertThat(isValid).isFalse();
        }

        @Test
        @DisplayName("Should return false for a null token")
        void shouldReturnFalseForNullToken() {
            // Act
            boolean isValid = jwtUtil.validateToken(null);

            // Assert
            assertThat(isValid).isFalse();
        }
    }

    // --- Helper Methods for generating specific test tokens ---

    /**
     * Generates a token with a custom expiration relative to the current time.
     * Uses the SAME secret key as the class under test.
     * @param username The subject of the token.
     * @param expirationOffsetInMillis A positive value for future expiration, negative for past.
     * @return A JWT string.
     */
    private String generateTokenWithCustomExpiration(String username, long expirationOffsetInMillis) {
        final String SECRET = "superSecretKeyThatShouldBeAtLeast32Characters!";
        final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationOffsetInMillis))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Generates a token with a DIFFERENT secret key to test signature validation.
     * @param username The subject of the token.
     * @return A JWT string signed with the wrong key.
     */
    private String generateTokenWithDifferentSecret(String username) {
        final String OTHER_SECRET = "aCompletelyDifferentSecretKeyThatIsAlsoSecure!";
        final Key wrongKey = Keys.hmacShaKeyFor(OTHER_SECRET.getBytes());
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 100000)) // Valid expiration
                .signWith(wrongKey, SignatureAlgorithm.HS256)
                .compact();
    }
}
