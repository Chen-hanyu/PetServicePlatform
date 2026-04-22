package com.petplatform.dto.ai;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record AiChatRequest(
        @NotEmpty(message = "Messages are required")
        @Size(max = 20, message = "Messages must not exceed 20 items")
        List<@Valid AiChatMessageRequest> messages
) {
}
