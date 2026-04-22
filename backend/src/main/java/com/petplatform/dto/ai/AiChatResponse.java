package com.petplatform.dto.ai;

import java.util.List;

public record AiChatResponse(
        String reply,
        List<String> suggestions
) {
}
