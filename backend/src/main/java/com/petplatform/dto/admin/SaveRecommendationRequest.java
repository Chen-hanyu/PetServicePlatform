package com.petplatform.dto.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SaveRecommendationRequest(
        @JsonProperty("biz_type")
        @NotBlank(message = "推荐对象类型不能为空")
        String bizType,
        @JsonProperty("biz_id")
        @NotNull(message = "推荐对象ID不能为空")
        Long bizId,
        @JsonProperty("slot_code")
        @NotBlank(message = "推荐位编码不能为空")
        String slotCode,
        @NotBlank(message = "状态不能为空")
        String status,
        Integer sort
) {
}
