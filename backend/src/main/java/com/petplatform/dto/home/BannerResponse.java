package com.petplatform.dto.home;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.petplatform.entity.Banner;

public record BannerResponse(
        Long id,
        String title,
        @JsonProperty("image_url") String imageUrl,
        @JsonProperty("link_url") String linkUrl
) {

    public static BannerResponse from(Banner banner) {
        return new BannerResponse(
                banner.getId(),
                banner.getTitle(),
                banner.getImageUrl(),
                banner.getLinkUrl()
        );
    }
}
