package com.petplatform.dto.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.petplatform.dto.community.PostAuthorResponse;

import java.time.LocalDateTime;

public record MerchantReviewResponse(
        Long id,
        int score,
        String content,
        PostAuthorResponse author,
        @JsonProperty("created_at") LocalDateTime createdAt
) {
}
