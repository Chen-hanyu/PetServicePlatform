package com.petplatform.dto.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.petplatform.entity.Recommendation;

public record AdminRecommendationResponse(
        Long id,
        @JsonProperty("biz_type") String bizType,
        @JsonProperty("biz_id") Long bizId,
        @JsonProperty("slot_code") String slotCode,
        String status,
        int sort,
        @JsonProperty("created_by") Long createdBy
) {

    public static AdminRecommendationResponse from(Recommendation recommendation) {
        return new AdminRecommendationResponse(
                recommendation.getId(),
                recommendation.getBizType(),
                recommendation.getBizId(),
                recommendation.getSlotCode(),
                recommendation.getStatus(),
                recommendation.getSort() == null ? 0 : recommendation.getSort(),
                recommendation.getCreatedBy()
        );
    }
}
