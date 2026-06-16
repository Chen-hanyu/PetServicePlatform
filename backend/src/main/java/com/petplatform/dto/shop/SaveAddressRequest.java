package com.petplatform.dto.shop;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record SaveAddressRequest(
        @JsonProperty("receiver_name") @NotBlank(message = "收货人不能为空") String receiverName,
        @JsonProperty("receiver_phone") @NotBlank(message = "联系电话不能为空") String receiverPhone,
        @NotBlank(message = "省份不能为空") String province,
        @NotBlank(message = "城市不能为空") String city,
        @NotBlank(message = "区县不能为空") String district,
        @JsonProperty("detail_address") @NotBlank(message = "详细地址不能为空") String detailAddress,
        @JsonProperty("is_default") Boolean isDefault
) {
}
