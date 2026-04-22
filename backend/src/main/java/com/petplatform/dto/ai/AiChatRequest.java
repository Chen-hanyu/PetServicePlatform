package com.petplatform.dto.ai;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record AiChatRequest(
        @NotEmpty(message = "Messages cannot be empty")
        List<ChatMessage> messages
) {
    public record ChatMessage(
            String role,
            String content
    ) {
    }
}
