package com.petplatform.dto.community;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ToggleFavoriteResponse(
        boolean favorited,
        @JsonProperty("favorite_count") int favoriteCount
) {
}
