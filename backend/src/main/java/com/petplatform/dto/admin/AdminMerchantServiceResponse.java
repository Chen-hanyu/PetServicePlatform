package com.petplatform.dto.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.petplatform.entity.MerchantService;

import java.math.BigDecimal;

public record AdminMerchantServiceResponse(
        Long id,
        @JsonProperty("merchant_id") Long merchantId,
        @JsonProperty("category_id") Long categoryId,
        String name,
        BigDecimal price,
        @JsonProperty("duration_minutes") Integer durationMinutes,
        String status
) {

    public static AdminMerchantServiceResponse from(MerchantService service) {
        return new AdminMerchantServiceResponse(
                service.getId(),
                service.getMerchantId(),
                service.getCategoryId(),
                service.getName(),
                service.getPrice(),
                service.getDurationMinutes(),
                service.getStatus()
        );
    }
}
