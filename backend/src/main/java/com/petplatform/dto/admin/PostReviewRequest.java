package com.petplatform.dto.admin;

import jakarta.validation.constraints.NotBlank;

public record PostReviewRequest(
        @NotBlank(message = "审核状态不能为空")
        String status,
        String remark
) {
}
