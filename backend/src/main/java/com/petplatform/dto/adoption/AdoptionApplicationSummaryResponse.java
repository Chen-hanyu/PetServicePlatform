package com.petplatform.dto.adoption;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record AdoptionApplicationSummaryResponse(
        Long id,
        AdoptionPetSummaryResponse pet,
        String status,
        @JsonProperty("review_remark") String reviewRemark,
        @JsonProperty("created_at") LocalDateTime createdAt
) {
}
