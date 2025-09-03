package com.simo.learnspringboot.learnspringboot.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record ForgetPasswordRequestDto(
        @NotNull
        @Email(message = "Should be a valid email address.")
        String email
) {
}
