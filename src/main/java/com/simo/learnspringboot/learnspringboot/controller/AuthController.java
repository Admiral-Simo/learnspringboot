package com.simo.learnspringboot.learnspringboot.controller;

import com.simo.learnspringboot.learnspringboot.dto.*;
import com.simo.learnspringboot.learnspringboot.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public AuthResponseDto register(@Valid @RequestBody RegisterRequestDto request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponseDto login(@RequestBody LoginRequestDto request) {
        return authService.login(request);
    }


    @PostMapping("/forget-password")
    public Map<String, String> forgetPassword(@Valid @RequestBody ForgetPasswordRequestDto request) {
        String message = authService.forgetPassword(request);
        return Map.of("message", message);
    }

    @PostMapping("/reset-password")
    public Map<String, String> resetPassword(@Valid @RequestBody ResetPasswordRequestDto request) {
        String message = authService.resetPassword(request);
        return Map.of("message", message);
    }


    @GetMapping("/verify-email")
    public Map<String, String> verifyEmail(@RequestParam("token") String token) {
        String message = authService.verifyEmail(token);
        return Map.of("message", message);
    }
}
