package com.petplatform.dto.pet;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.petplatform.entity.PetWeight;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PetWeightResponse(
        Long id,
        BigDecimal weight,
        @JsonProperty("recorded_at") LocalDateTime recordedAt
) {

    public static PetWeightResponse from(PetWeight petWeight) {
        return new PetWeightResponse(
                petWeight.getId(),
                petWeight.getWeight(),
                petWeight.getRecordedAt()
        );
    }
}
