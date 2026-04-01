package com.petplatform.dto.shop;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AddCartItemRequest(
        @JsonProperty("product_id")
        @NotNull(message = "商品ID不能为空")
        Long productId,

        @NotNull(message = "商品数量不能为空")
        @Min(value = 1, message = "商品数量必须大于等于1")
        Integer quantity
) {
}
