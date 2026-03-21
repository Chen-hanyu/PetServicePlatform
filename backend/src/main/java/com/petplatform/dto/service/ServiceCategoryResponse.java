package com.petplatform.dto.service;

import com.petplatform.entity.ServiceCategory;

public record ServiceCategoryResponse(Long id, String name, int sort, String status) {

    public static ServiceCategoryResponse from(ServiceCategory category) {
        return new ServiceCategoryResponse(
                category.getId(),
                category.getName(),
                category.getSort() == null ? 0 : category.getSort(),
                category.getStatus()
        );
    }
}
