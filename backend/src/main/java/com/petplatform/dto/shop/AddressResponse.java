package com.petplatform.dto.shop;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.petplatform.entity.UserAddress;

public record AddressResponse(
        Long id,
        @JsonProperty("receiver_name") String receiverName,
        @JsonProperty("receiver_phone") String receiverPhone,
        String province,
        String city,
        String district,
        @JsonProperty("detail_address") String detailAddress,
        @JsonProperty("full_address") String fullAddress,
        @JsonProperty("is_default") Boolean isDefault
) {
    public static AddressResponse from(UserAddress address) {
        return new AddressResponse(
                address.getId(),
                address.getReceiverName(),
                address.getReceiverPhone(),
                address.getProvince(),
                address.getCity(),
                address.getDistrict(),
                address.getDetailAddress(),
                joinAddress(address),
                Boolean.TRUE.equals(address.getIsDefault())
        );
    }

    private static String joinAddress(UserAddress address) {
        return String.join(" ",
                address.getProvince(),
                address.getCity(),
                address.getDistrict(),
                address.getDetailAddress()
        ).replaceAll("\\s+", " ").trim();
    }
}
