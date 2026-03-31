package com.petplatform.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank(message = "Phone is required")
        @Pattern(regexp = "^1\\d{10}$", message = "Phone format is invalid")
        String phone,

        @NotBlank(message = "Password is required")
        @Size(min = 6, max = 20, message = "Password length must be between 6 and 20")
        String password
) {
}
