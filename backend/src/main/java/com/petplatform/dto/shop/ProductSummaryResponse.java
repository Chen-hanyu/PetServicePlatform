package com.petplatform.dto.shop;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.petplatform.entity.Product;

import java.math.BigDecimal;

public record ProductSummaryResponse(
        Long id,
        @JsonProperty("category_id") Long categoryId,
        String name,
        String subtitle,
        @JsonProperty("image_url") String imageUrl,
        BigDecimal price,
        int stock,
        @JsonProperty("pet_type") String petType,
        String status
) {

    public static ProductSummaryResponse from(Product product) {
        return new ProductSummaryResponse(
                product.getId(),
                product.getCategoryId(),
                product.getName(),
                product.getSubtitle(),
                product.getImageUrl(),
                product.getPrice(),
                product.getStock() == null ? 0 : product.getStock(),
                product.getPetType(),
                product.getStatus()
        );
    }
}
