package com.petplatform.dto.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.petplatform.entity.Merchant;

import java.math.BigDecimal;

public record AdminMerchantResponse(
        Long id,
        String name,
        String district,
        String address,
        String phone,
        @JsonProperty("business_hours") String businessHours,
        BigDecimal score,
        String status
) {

    public static AdminMerchantResponse from(Merchant merchant) {
        return new AdminMerchantResponse(
                merchant.getId(),
                merchant.getName(),
                merchant.getDistrict(),
                merchant.getAddress(),
                merchant.getPhone(),
                merchant.getBusinessHours(),
                merchant.getScore(),
                merchant.getStatus()
        );
    }
}
