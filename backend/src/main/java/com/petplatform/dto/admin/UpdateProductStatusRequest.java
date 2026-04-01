package com.petplatform.dto.admin;

import jakarta.validation.constraints.NotBlank;

public record UpdateProductStatusRequest(
        @NotBlank(message = "商品状态不能为空")
        String status
) {
}
