package com.petplatform.dto.admin;

import jakarta.validation.constraints.NotBlank;

public record UpdateUserStatusRequest(
        @NotBlank(message = "用户状态不能为空")
        String status,
        String remark
) {
}
