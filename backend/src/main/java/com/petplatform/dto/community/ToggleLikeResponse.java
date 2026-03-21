package com.petplatform.dto.community;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ToggleLikeResponse(
        boolean liked,
        @JsonProperty("like_count") int likeCount
) {
}
