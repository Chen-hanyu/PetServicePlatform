package com.petplatform.dto.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.petplatform.entity.Merchant;

import java.math.BigDecimal;
import java.util.List;

public record MerchantDetailResponse(
        Long id,
        String name,
        String district,
        String address,
        String phone,
        @JsonProperty("business_hours") String businessHours,
        BigDecimal score,
        List<MerchantServiceResponse> services,
        List<MerchantReviewResponse> reviews
) {

    public static MerchantDetailResponse from(
            Merchant merchant,
            List<MerchantServiceResponse> services,
            List<MerchantReviewResponse> reviews
    ) {
        return new MerchantDetailResponse(
                merchant.getId(),
                merchant.getName(),
                merchant.getDistrict(),
                merchant.getAddress(),
                merchant.getPhone(),
                merchant.getBusinessHours(),
                merchant.getScore(),
                services,
                reviews
        );
    }
}
