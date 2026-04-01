package com.petplatform.dto.service;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record CreateMerchantReviewResponse(
        Long id,
        int score,
        @JsonProperty("merchant_score") BigDecimal merchantScore
) {
}
