package com.petplatform.dto.message;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MarkMessageReadResponse(
        Long id,
        @JsonProperty("is_read") boolean isRead
) {
}
