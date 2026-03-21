package com.petplatform.dto.admin;

import jakarta.validation.constraints.NotBlank;

public record SaveServiceCategoryRequest(
        @NotBlank(message = "分类名称不能为空")
        String name,
        Integer sort,
        @NotBlank(message = "状态不能为空")
        String status
) {
}
