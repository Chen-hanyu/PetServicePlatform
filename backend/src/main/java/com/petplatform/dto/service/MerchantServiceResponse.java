package com.petplatform.dto.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.petplatform.entity.MerchantService;

import java.math.BigDecimal;

public record MerchantServiceResponse(
        Long id,
        @JsonProperty("category_id") Long categoryId,
        String name,
        BigDecimal price,
        @JsonProperty("duration_minutes") Integer durationMinutes,
        String status
) {

    public static MerchantServiceResponse from(MerchantService service) {
        return new MerchantServiceResponse(
                service.getId(),
                service.getCategoryId(),
                service.getName(),
                service.getPrice(),
                service.getDurationMinutes(),
                service.getStatus()
        );
    }
}
