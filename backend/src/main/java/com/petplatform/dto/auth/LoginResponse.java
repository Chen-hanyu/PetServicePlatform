package com.petplatform.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.petplatform.dto.user.UserProfileResponse;

public record LoginResponse(
        String token,
        @JsonProperty("token_type") String tokenType,
        @JsonProperty("expires_in") long expiresIn,
        UserProfileResponse user
) {
}
