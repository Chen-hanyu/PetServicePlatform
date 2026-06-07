package com.petplatform.dto.shop;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

import java.util.List;

public record CreateDirectOrderRequest(
        @Valid
        @NotEmpty(message = "商品不能为空")
        List<OrderProductRequest> items,

        @JsonProperty("address_id")
        Long addressId,

        @JsonProperty("coupon_id")
        Long couponId,

        @JsonProperty("receiver_name")
        @NotBlank(message = "收货人不能为空")
        String receiverName,

        @JsonProperty("receiver_phone")
        @NotBlank(message = "收货电话不能为空")
        @Pattern(regexp = "^1\\d{10}$", message = "手机号格式不正确")
        String receiverPhone,

        @JsonProperty("receiver_address")
        @NotBlank(message = "收货地址不能为空")
        String receiverAddress,

        String remark
) {
}
