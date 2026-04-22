package com.petplatform.dto.ai;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AiChatMessageRequest(
        @NotBlank(message = "Message role is required")
        @Pattern(regexp = "^(user|assistant)$", message = "Message role must be user or assistant")
        String role,

        @NotBlank(message = "Message content is required")
        @Size(max = 2000, message = "Message content must not exceed 2000 characters")
        String content
) {
}
