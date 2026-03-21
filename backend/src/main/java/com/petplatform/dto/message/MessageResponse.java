package com.petplatform.dto.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.petplatform.entity.Message;

import java.time.LocalDateTime;

public record MessageResponse(
        Long id,
        String type,
        String title,
        String content,
        @JsonProperty("is_read") boolean isRead,
        @JsonProperty("created_at") LocalDateTime createdAt
) {

    public static MessageResponse from(Message message) {
        return new MessageResponse(
                message.getId(),
                message.getType(),
                message.getTitle(),
                message.getContent(),
                Boolean.TRUE.equals(message.getRead()),
                message.getCreatedAt()
        );
    }
}
