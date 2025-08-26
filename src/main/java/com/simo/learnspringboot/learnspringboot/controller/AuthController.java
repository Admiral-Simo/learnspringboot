package com.simo.learnspringboot.learnspringboot.controller;

import com.simo.learnspringboot.learnspringboot.dto.AuthResponseDto;
import com.simo.learnspringboot.learnspringboot.dto.LoginRequestDto;
import com.simo.learnspringboot.learnspringboot.dto.RegisterRequestDto;
import com.simo.learnspringboot.learnspringboot.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public String register(@Valid @RequestBody RegisterRequestDto request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponseDto login(@Valid @RequestBody LoginRequestDto request) {
        return authService.login(request);
    }
}
