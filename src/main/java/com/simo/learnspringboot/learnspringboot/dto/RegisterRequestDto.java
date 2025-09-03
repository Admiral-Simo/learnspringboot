package com.simo.learnspringboot.learnspringboot.dto;

import com.simo.learnspringboot.learnspringboot.validation.ValidationGroups;
import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


// Use the GroupSequence to enforce a strict validation order.
@GroupSequence({RegisterRequestDto.class, ValidationGroups.FirstChecks.class, ValidationGroups.SecondChecks.class})
public record RegisterRequestDto(
        @NotBlank(
                message = "name is required"
        )
        String name,

        @NotBlank(
                message = "email is required",
                // This check runs first
                groups = ValidationGroups.FirstChecks.class
        )
        @Email(
                message = "email should be valid",
                // This check runs second
                groups = ValidationGroups.SecondChecks.class
        )
        String email,

        @NotBlank(
                message = "password is required",
                // This check runs first
                groups = ValidationGroups.FirstChecks.class
        )
        @Size(
                max = 20,
                message = "password length must be less than 20 characters",
                // This check runs second
                groups = ValidationGroups.SecondChecks.class
        )
        @Pattern(
                regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&+=])(?=\\S+$).{8,}$",
                message = "password size should be between 8 and 20, contain at least one digit, one lowercase letter, one uppercase letter, and one special character.",
                // This check also runs second
                groups = ValidationGroups.SecondChecks.class
        )
        String password
) {
}
