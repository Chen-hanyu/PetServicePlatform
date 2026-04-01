package com.petplatform.dto.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record SaveMerchantRequest(
        @NotBlank(message = "商家名称不能为空")
        String name,
        @NotBlank(message = "区域不能为空")
        String district,
        @NotBlank(message = "地址不能为空")
        String address,
        @NotBlank(message = "联系电话不能为空")
        String phone,
        @JsonProperty("business_hours")
        @NotBlank(message = "营业时间不能为空")
        String businessHours,
        @NotBlank(message = "状态不能为空")
        String status
) {
}
