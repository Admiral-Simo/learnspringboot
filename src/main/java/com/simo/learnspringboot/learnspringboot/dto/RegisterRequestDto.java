package com.simo.learnspringboot.learnspringboot.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequestDto(
        @NotBlank(message = "name is required")
        String name,
        @Email(message = "email should be valid")
        @NotBlank(message = "email is required")
        String email,
        @NotBlank(message = "password is required")
        @Size(max = 20, message = "password length must be less than 20 characters")
        @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&+=])(?=\\S+$).{8,}$",
                message = "password size should be between 8 and 20, contain at least one digit, one lowercase letter, one uppercase letter, and one special character.")
        String password
        ) {
}
