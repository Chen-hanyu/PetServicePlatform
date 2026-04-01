package com.petplatform.dto.shop;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.petplatform.entity.Product;

import java.math.BigDecimal;
import java.util.List;

public record ProductDetailResponse(
        Long id,
        @JsonProperty("category_id") Long categoryId,
        String name,
        String subtitle,
        @JsonProperty("image_url") String imageUrl,
        List<String> images,
        BigDecimal price,
        int stock,
        @JsonProperty("pet_type") String petType,
        String description,
        String status
) {

    public static ProductDetailResponse from(Product product) {
        List<String> images = product.getImageUrl() == null || product.getImageUrl().isBlank()
                ? List.of()
                : List.of(product.getImageUrl());
        return new ProductDetailResponse(
                product.getId(),
                product.getCategoryId(),
                product.getName(),
                product.getSubtitle(),
                product.getImageUrl(),
                images,
                product.getPrice(),
                product.getStock() == null ? 0 : product.getStock(),
                product.getPetType(),
                product.getDescription(),
                product.getStatus()
        );
    }
}
