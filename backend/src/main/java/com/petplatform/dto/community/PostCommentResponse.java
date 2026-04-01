package com.petplatform.dto.community;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record PostCommentResponse(
        Long id,
        String content,
        PostCommentAuthorResponse author,
        @JsonProperty("created_at") LocalDateTime createdAt
) {
}
