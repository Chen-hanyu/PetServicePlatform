package com.petplatform.dto.profile;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.petplatform.dto.user.UserProfileResponse;

public record ProfileOverviewResponse(
        UserProfileResponse user,
        @JsonProperty("pet_count") long petCount,
        @JsonProperty("post_count") long postCount,
        @JsonProperty("favorite_count") long favoriteCount,
        @JsonProperty("order_count") long orderCount,
        @JsonProperty("booking_count") long bookingCount,
        @JsonProperty("adoption_application_count") long adoptionApplicationCount,
        @JsonProperty("unread_message_count") long unreadMessageCount
) {
}
