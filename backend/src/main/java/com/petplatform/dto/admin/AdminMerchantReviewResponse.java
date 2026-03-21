package com.petplatform.dto.admin;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record AdminMerchantReviewResponse(
        Long id,
        @JsonProperty("merchant_id") Long merchantId,
        @JsonProperty("merchant_name") String merchantName,
        int score,
        String content,
        Author author,
        @JsonProperty("created_at") LocalDateTime createdAt
) {
    public record Author(Long id, String nickname, String phone) {
    }
}
