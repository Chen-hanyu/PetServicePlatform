package com.petplatform.dto.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.petplatform.entity.Product;

import java.math.BigDecimal;

public record AdminProductResponse(
        Long id,
        @JsonProperty("category_id") Long categoryId,
        String name,
        String subtitle,
        @JsonProperty("image_url") String imageUrl,
        BigDecimal price,
        int stock,
        @JsonProperty("pet_type") String petType,
        String description,
        String status
) {

    public static AdminProductResponse from(Product product) {
        return new AdminProductResponse(
                product.getId(),
                product.getCategoryId(),
                product.getName(),
                product.getSubtitle(),
                product.getImageUrl(),
                product.getPrice(),
                product.getStock() == null ? 0 : product.getStock(),
                product.getPetType(),
                product.getDescription(),
                product.getStatus()
        );
    }
}
