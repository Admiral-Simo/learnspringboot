package com.simo.learnspringboot.learnspringboot.service;

import com.simo.learnspringboot.learnspringboot.dto.*;
import com.simo.learnspringboot.learnspringboot.exception_handler.exceptions.EmailAlreadyInUseException;
import com.simo.learnspringboot.learnspringboot.exception_handler.exceptions.InvalidCredentialsException;
import com.simo.learnspringboot.learnspringboot.exception_handler.exceptions.InvalidTokenException;
import com.simo.learnspringboot.learnspringboot.model.User;
import com.simo.learnspringboot.learnspringboot.repository.UserRepository;
import com.simo.learnspringboot.learnspringboot.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    public AuthService(AuthenticationManager authenticationManager,
                       UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       EmailService emailService,
                       JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.emailService = emailService;
    }

    public AuthResponseDto register(RegisterRequestDto request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new EmailAlreadyInUseException("Email already in use!");
        }

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));

        if (request.email().equals("simo@gmail.com")) {
            user.setRole("ROLE_ADMIN");
        } else {
            user.setRole("ROLE_USER");
        }

        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getEmail());

        return new AuthResponseDto(
                token,
                user.getEmail(),
                user.getRole(),
                "User registered successfully!"
        );
    }

    public AuthResponseDto login(LoginRequestDto request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(), request.password()
                )
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new InvalidCredentialsException("Email or password is incorrect."));

        String token = jwtUtil.generateToken(user.getEmail());

        return new AuthResponseDto(
                token,
                user.getEmail(),
                user.getRole(),
                "Logged in successfully!"
        );
    }

    public String forgetPassword(ForgetPasswordRequestDto request) {
        userRepository.findByEmail(request.email()).ifPresent(user -> {
            String token = UUID.randomUUID().toString();
            user.setPasswordResetToken(token);
            user.setTokenExpiryDate(LocalDateTime.now().plusMinutes(20));
            userRepository.save(user);
            emailService.sendResetPasswordEmail(user.getEmail(), token);
        });

        return "If an account with that email exists, a password reset link has been sent.";
    }

    public String resetPassword(ResetPasswordRequestDto request) {
        User user = userRepository.findByPasswordResetToken(request.token())
                .orElseThrow(() -> new InvalidTokenException("Invalid or expired password reset token."));

        if (user.getTokenExpiryDate().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("Invalid or expired password reset token.");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        user.setPasswordResetToken(null);
        user.setTokenExpiryDate(null);
        userRepository.save(user);

        return "Password has been successfully reset.";
    }
}
