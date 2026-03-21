package com.petplatform.dto.pet;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreatePetWeightRequest(
        @NotNull(message = "体重不能为空")
        @DecimalMin(value = "0.01", message = "体重必须大于0")
        BigDecimal weight,

        @JsonProperty("recorded_at")
        LocalDateTime recordedAt
) {
}
