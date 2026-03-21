package com.petplatform.dto.admin;

import jakarta.validation.constraints.NotBlank;

public record SaveTagRequest(
        @NotBlank(message = "标签名称不能为空")
        String name,
        @NotBlank(message = "标签类型不能为空")
        String type,
        @NotBlank(message = "状态不能为空")
        String status,
        Integer sort
) {
}
