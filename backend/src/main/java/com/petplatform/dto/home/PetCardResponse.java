package com.petplatform.dto.home;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PetCardResponse(
        String title,
        String subtitle,
        @JsonProperty("image_url") String imageUrl
) {
}
