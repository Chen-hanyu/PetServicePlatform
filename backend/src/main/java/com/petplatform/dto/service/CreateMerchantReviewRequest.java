package com.petplatform.dto.service;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateMerchantReviewRequest(
        @NotNull(message = "评分不能为空")
        @Min(value = 1, message = "评分不能低于 1")
        @Max(value = 5, message = "评分不能高于 5")
        Integer score,
        @NotBlank(message = "评价内容不能为空")
        String content
) {
}
