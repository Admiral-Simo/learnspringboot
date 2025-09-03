package com.simo.learnspringboot.learnspringboot.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record ResetPasswordRequestDto(
        @NotNull(message = "Token must not be null.")
        String token,
        @NotNull(message = "New password must not be null.")
        @Pattern(
                regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&+=])(?=\\S+$).{8,}$",
                message = "password size should be between 8 and 20, contain at least one digit, one lowercase letter, one uppercase letter, and one special character."
        )
        String newPassword
) {
}
