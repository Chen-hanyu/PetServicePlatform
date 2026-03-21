package com.petplatform.dto.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.petplatform.entity.Merchant;

import java.math.BigDecimal;

public record MerchantSummaryResponse(
        Long id,
        String name,
        String district,
        String address,
        BigDecimal score,
        @JsonProperty("business_hours") String businessHours,
        String status
) {

    public static MerchantSummaryResponse from(Merchant merchant) {
        return new MerchantSummaryResponse(
                merchant.getId(),
                merchant.getName(),
                merchant.getDistrict(),
                merchant.getAddress(),
                merchant.getScore(),
                merchant.getBusinessHours(),
                merchant.getStatus()
        );
    }
}
