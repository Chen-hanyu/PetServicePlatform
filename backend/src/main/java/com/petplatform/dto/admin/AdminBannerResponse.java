package com.petplatform.dto.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.petplatform.entity.Banner;

public record AdminBannerResponse(
        Long id,
        String title,
        @JsonProperty("image_url") String imageUrl,
        @JsonProperty("link_url") String linkUrl,
        String status,
        int sort
) {

    public static AdminBannerResponse from(Banner banner) {
        return new AdminBannerResponse(
                banner.getId(),
                banner.getTitle(),
                banner.getImageUrl(),
                banner.getLinkUrl(),
                banner.getStatus(),
                banner.getSort() == null ? 0 : banner.getSort()
        );
    }
}
