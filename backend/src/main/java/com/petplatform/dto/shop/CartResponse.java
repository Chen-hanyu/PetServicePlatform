package com.petplatform.dto.shop;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse(
        List<CartItemResponse> items,
        @JsonProperty("total_amount") BigDecimal totalAmount
) {
}
