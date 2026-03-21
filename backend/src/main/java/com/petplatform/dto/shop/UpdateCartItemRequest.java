package com.petplatform.dto.shop;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateCartItemRequest(
        @NotNull(message = "商品数量不能为空")
        @Min(value = 1, message = "商品数量必须大于等于1")
        Integer quantity,
        Boolean checked
) {
}
