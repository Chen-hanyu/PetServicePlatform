package com.petplatform.dto.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record SaveBannerRequest(
        @NotBlank(message = "标题不能为空")
        String title,
        @JsonProperty("image_url")
        @NotBlank(message = "图片地址不能为空")
        String imageUrl,
        @JsonProperty("link_url")
        String linkUrl,
        @NotBlank(message = "状态不能为空")
        String status,
        Integer sort
) {
}
