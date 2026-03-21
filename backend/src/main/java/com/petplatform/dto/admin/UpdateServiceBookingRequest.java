package com.petplatform.dto.admin;

import jakarta.validation.constraints.NotBlank;

public record UpdateServiceBookingRequest(
        @NotBlank(message = "预约状态不能为空")
        String status,
        String remark
) {
}
