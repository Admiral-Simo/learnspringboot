package com.simo.learnspringboot.learnspringboot.service;

import com.simo.learnspringboot.learnspringboot.dto.*;
import com.simo.learnspringboot.learnspringboot.exception_handler.exceptions.EmailAlreadyInUseException;
import com.simo.learnspringboot.learnspringboot.exception_handler.exceptions.InvalidAuthCredentialsException;
import com.simo.learnspringboot.learnspringboot.exception_handler.exceptions.InvalidTokenException;
import com.simo.learnspringboot.learnspringboot.model.User;
import com.simo.learnspringboot.learnspringboot.repository.UserRepository;
import com.simo.learnspringboot.learnspringboot.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthService authService;

    @Test
    void shouldRegisterNewUserSuccessfullyAndAssignAdminToSimoEmail() {
        RegisterRequestDto request = new RegisterRequestDto(
                "Simo",
                "mohamedkhalisgm@gmail.com",
                "Password@123"
        );

        when(userRepository.findByEmail("mohamedkhalisgm@gmail.com"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode("Password@123"))
                .thenReturn("encodedPassword");

        AuthResponseDto responseDto = authService.register(request);

        assertThat(responseDto).isNotNull();
        assertThat(responseDto.email()).isNotNull();
        assertThat(responseDto.token()).isNull();
        assertThat(responseDto.role()).isEqualTo("ROLE_ADMIN");
        assertThat(responseDto.message()).isEqualTo("User registered successfully!");

        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_ShouldCreateUnverifiedUserWithTokenAndSendEmail() {
        // Arrange
        RegisterRequestDto request = new RegisterRequestDto("NewUser", "new@example.com", "Password@123");
        when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");

        // Use an ArgumentCaptor to inspect the user object that gets saved
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        // Act
        authService.register(request);

        // Assert
        // Verify that the save method was called and capture the user
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertThat(savedUser.isVerified()).isFalse(); // Must be unverified
        assertThat(savedUser.getEmailVerificationToken()).isNotNull().isNotEmpty(); // Must have a token

        // Verify an email was sent with the correct details
        verify(emailService).sendVerificationEmail(eq(savedUser.getEmail()), eq(savedUser.getEmailVerificationToken()));
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

        AuthResponseDto responseDto = authService.register(request);

        assertThat(responseDto).isNotNull();
        assertThat(responseDto.email()).isNotNull();
        assertThat(responseDto.token()).isNull();
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

        EmailAlreadyInUseException thrown = assertThrows(EmailAlreadyInUseException.class, () -> authService.register(request));
        assertThat(thrown).isNotNull();
        assertThat(thrown.getMessage()).isEqualTo("Email already in use!");

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

    @Test
    void shouldThrowExceptionIfLoginCredentialsAreInvalid() {
        LoginRequestDto request = new LoginRequestDto("alice@example.com", "password123");

        // Mock authentication manager (does nothing in unit test)
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(Authentication.class));

        when(userRepository.findByEmail("alice@example.com"))
                .thenReturn(Optional.empty());

        InvalidAuthCredentialsException thrown = assertThrows(InvalidAuthCredentialsException.class, () -> authService.login(request));

        assertThat(thrown).isNotNull();
        assertThat(thrown.getMessage()).isEqualTo("Email or password is incorrect.");
    }

    @Test
    void forgetPassword_UserExists_ShouldGenerateTokenAndSendEmail() {
        String email = "user@gmail.com";
        var request = new ForgetPasswordRequestDto(email);
        User user = new User();
        user.setEmail(email);

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        String message = authService.forgetPassword(request);

        verify(userRepository).save(any(User.class));
        verify(emailService).sendResetPasswordEmail(eq(email), anyString());
        assertThat(message).isEqualTo("If an account with that email exists, a password reset link has been sent.");
    }

    @Test
    void forgetPassword_UserDoesNotExists_ShouldNotSendEmail() {
        String email = "user@gmail.com";
        var request = new ForgetPasswordRequestDto(email);
        when(userRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        String message = authService.forgetPassword(request);

        verify(userRepository, never()).save(any(User.class));
        verify(emailService, never()).sendResetPasswordEmail(anyString(), anyString());
        assertThat(message).isEqualTo("If an account with that email exists, a password reset link has been sent.");
    }

    @Test
    void resetPassword_ValidToken_ShouldUpdatePassword() {
        var request = new ResetPasswordRequestDto("mockToken", "NewPassword@123");
        User user = new User();
        user.setTokenExpiryDate(LocalDateTime.now().plusMinutes(5));

        when(passwordEncoder.encode("NewPassword@123"))
                .thenReturn("encodedNewPassword");

        when(userRepository.findByPasswordResetToken("mockToken"))
                .thenReturn(Optional.of(user));

        String message = authService.resetPassword(request);

        assertThat(message).isEqualTo("Password has been successfully reset.");
        assertThat(user.getPassword()).isEqualTo("encodedNewPassword");
        assertThat(user.getPasswordResetToken()).isNull();
        verify(userRepository).save(user);
    }

    @Test
    void resetPassword_InvalidToken_ShouldThrowException() {
        var request = new ResetPasswordRequestDto("invalidToken", "NewPassword@123");

        when(userRepository.findByPasswordResetToken("invalidToken"))
                .thenReturn(Optional.empty());

        InvalidTokenException thrown = assertThrows(InvalidTokenException.class, () -> authService.resetPassword(request) );

        assertThat(thrown).isNotNull();
        assertThat(thrown.getMessage()).isEqualTo("Invalid or expired password reset token.");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void verifyEmail_ValidToken_ShouldVerifyUser() {
        String token = "validToken";
        User unverifiedUser = new User();
        unverifiedUser.setVerified(false);
        unverifiedUser.setEmailVerificationToken(token);

        when(userRepository.findByEmailVerificationToken(token))
                .thenReturn(Optional.of(unverifiedUser));

        ArgumentCaptor<User> argumentCaptor = ArgumentCaptor.forClass(User.class);

        String responseMessage = authService.verifyEmail(token);

        verify(userRepository).save(argumentCaptor.capture());
        User savedUser = argumentCaptor.getValue();

        assertThat(responseMessage).isEqualTo("Email verified successfully!");
        assertThat(savedUser.isVerified()).isTrue();
        assertThat(savedUser.getEmailVerificationToken()).isNull();
    }


    @Test
    void verifyEmail_InvalidToken_Failure() {
        String token = "invalidToken";
        User unverifiedUser = new User();
        unverifiedUser.setVerified(false);
        unverifiedUser.setEmailVerificationToken(token);

        when(userRepository.findByEmailVerificationToken(token))
                .thenReturn(Optional.empty());

        InvalidTokenException thrown = assertThrows(InvalidTokenException.class, () -> authService.verifyEmail(token));

        assertThat(thrown).isNotNull();
        assertThat(thrown.getMessage()).isEqualTo("Invalid email verification token.");
    }
}
