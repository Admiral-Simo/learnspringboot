package com.simo.learnspringboot.learnspringboot.service;

import com.simo.learnspringboot.learnspringboot.dto.AuthResponseDto;
import com.simo.learnspringboot.learnspringboot.dto.LoginRequestDto;
import com.simo.learnspringboot.learnspringboot.dto.RegisterRequestDto;
import com.simo.learnspringboot.learnspringboot.exception_handler.exceptions.EmailAlreadyInUseException;
import com.simo.learnspringboot.learnspringboot.exception_handler.exceptions.InvalidCredentialsException;
import com.simo.learnspringboot.learnspringboot.model.User;
import com.simo.learnspringboot.learnspringboot.repository.UserRepository;
import com.simo.learnspringboot.learnspringboot.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(AuthenticationManager authenticationManager,
                       UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
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
}
