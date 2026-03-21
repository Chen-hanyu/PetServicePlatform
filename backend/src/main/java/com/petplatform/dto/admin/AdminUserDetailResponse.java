package com.petplatform.dto.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.petplatform.dto.user.UserProfileResponse;

public record AdminUserDetailResponse(
        UserProfileResponse user,
        @JsonProperty("pet_count") long petCount,
        @JsonProperty("post_count") long postCount,
        @JsonProperty("order_count") long orderCount,
        @JsonProperty("adoption_application_count") long adoptionApplicationCount
) {
}
