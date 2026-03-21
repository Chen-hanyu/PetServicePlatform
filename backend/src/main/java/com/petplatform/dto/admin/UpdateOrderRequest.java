package com.petplatform.dto.admin;

import jakarta.validation.constraints.NotBlank;

public record UpdateOrderRequest(
        @NotBlank(message = "订单状态不能为空")
        String status,
        String remark
) {
}
