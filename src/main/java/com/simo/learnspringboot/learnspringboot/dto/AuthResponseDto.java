package com.simo.learnspringboot.learnspringboot.dto;

public record AuthResponseDto(
        String token,
        String email,
        String role
) {}
