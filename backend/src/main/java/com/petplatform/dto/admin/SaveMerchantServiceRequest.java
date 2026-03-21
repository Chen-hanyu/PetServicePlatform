package com.petplatform.dto.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record SaveMerchantServiceRequest(
        @JsonProperty("merchant_id")
        @NotNull(message = "商家ID不能为空")
        Long merchantId,
        @JsonProperty("category_id")
        @NotNull(message = "服务分类ID不能为空")
        Long categoryId,
        @NotBlank(message = "服务项目名称不能为空")
        String name,
        @NotNull(message = "价格不能为空")
        @DecimalMin(value = "0.01", message = "价格必须大于 0")
        BigDecimal price,
        @JsonProperty("duration_minutes")
        @NotNull(message = "服务时长不能为空")
        @Min(value = 1, message = "服务时长必须大于 0")
        Integer durationMinutes,
        @NotBlank(message = "状态不能为空")
        String status
) {
}
