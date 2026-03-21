package com.petplatform.dto.shop;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record OrderItemResponse(
        Long id,
        @JsonProperty("product_id") Long productId,
        @JsonProperty("product_name") String productName,
        @JsonProperty("product_image_url") String productImageUrl,
        @JsonProperty("unit_price") BigDecimal unitPrice,
        int quantity,
        @JsonProperty("subtotal_amount") BigDecimal subtotalAmount
) {
}
