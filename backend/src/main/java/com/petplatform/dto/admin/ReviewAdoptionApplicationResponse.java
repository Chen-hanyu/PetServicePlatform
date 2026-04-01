package com.petplatform.dto.admin;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ReviewAdoptionApplicationResponse(
        Long id,
        String status,
        @JsonProperty("review_remark") String reviewRemark
) {
}
