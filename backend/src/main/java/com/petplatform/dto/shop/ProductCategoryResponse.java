package com.petplatform.dto.shop;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.petplatform.entity.ProductCategory;

public record ProductCategoryResponse(
        Long id,
        String name,
        @JsonProperty("pet_type") String petType,
        int sort,
        String status
) {

    public static ProductCategoryResponse from(ProductCategory category) {
        return new ProductCategoryResponse(
                category.getId(),
                category.getName(),
                category.getPetType(),
                category.getSort() == null ? 0 : category.getSort(),
                category.getStatus()
        );
    }
}
