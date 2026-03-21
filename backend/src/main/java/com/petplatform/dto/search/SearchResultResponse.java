package com.petplatform.dto.search;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SearchResultResponse(
        String module,
        Long id,
        String title,
        String subtitle,
        @JsonProperty("image_url") String imageUrl,
        String status
) {
}
