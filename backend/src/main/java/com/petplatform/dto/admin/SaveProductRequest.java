package com.petplatform.dto.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record SaveProductRequest(
        @JsonProperty("category_id")
        @NotNull(message = "分类ID不能为空")
        Long categoryId,
        @NotBlank(message = "商品名称不能为空")
        String name,
        String subtitle,
        @JsonProperty("image_url")
        @NotBlank(message = "主图不能为空")
        String imageUrl,
        @NotNull(message = "售价不能为空")
        @DecimalMin(value = "0.01", message = "售价必须大于0")
        BigDecimal price,
        @NotNull(message = "库存不能为空")
        @Min(value = 0, message = "库存不能小于0")
        Integer stock,
        @JsonProperty("pet_type") String petType,
        String description,
        @NotBlank(message = "状态不能为空")
        String status
) {
}
