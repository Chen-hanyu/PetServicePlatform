package com.petplatform.dto.pet;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record CreatePetAlbumRequest(
        @JsonProperty("image_url")
        @NotBlank(message = "相册图片地址不能为空")
        String imageUrl,
        String caption
) {
}
