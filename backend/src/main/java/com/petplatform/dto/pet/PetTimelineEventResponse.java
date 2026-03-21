package com.petplatform.dto.pet;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record PetTimelineEventResponse(
        String type,
        String title,
        String description,
        @JsonProperty("occurred_at") LocalDateTime occurredAt,
        @JsonProperty("image_url") String imageUrl
) {
}
