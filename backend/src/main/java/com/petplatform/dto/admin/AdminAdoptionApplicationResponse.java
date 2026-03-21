package com.petplatform.dto.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.petplatform.dto.adoption.AdoptionPetSummaryResponse;
import com.petplatform.dto.user.UserProfileResponse;

import java.time.LocalDateTime;

public record AdminAdoptionApplicationResponse(
        Long id,
        AdoptionPetSummaryResponse pet,
        UserProfileResponse user,
        @JsonProperty("contact_phone") String contactPhone,
        @JsonProperty("experience_desc") String experienceDesc,
        @JsonProperty("living_condition_desc") String livingConditionDesc,
        String status,
        @JsonProperty("review_remark") String reviewRemark,
        @JsonProperty("created_at") LocalDateTime createdAt,
        @JsonProperty("reviewed_at") LocalDateTime reviewedAt
) {
}
