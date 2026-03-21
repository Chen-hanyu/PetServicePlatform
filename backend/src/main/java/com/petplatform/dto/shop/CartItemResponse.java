package com.petplatform.dto.shop;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record CartItemResponse(
        Long id,
        @JsonProperty("product_id") Long productId,
        String name,
        String subtitle,
        @JsonProperty("image_url") String imageUrl,
        BigDecimal price,
        int quantity,
        boolean checked,
        @JsonProperty("subtotal_amount") BigDecimal subtotalAmount
) {
}
