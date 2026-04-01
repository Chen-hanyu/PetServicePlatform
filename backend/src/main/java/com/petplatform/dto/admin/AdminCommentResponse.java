package com.petplatform.dto.admin;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record AdminCommentResponse(
        Long id,
        @JsonProperty("post_id") Long postId,
        @JsonProperty("post_title") String postTitle,
        String content,
        Author author,
        String status,
        @JsonProperty("created_at") LocalDateTime createdAt
) {
    public record Author(Long id, String nickname, String phone) {
    }
}
