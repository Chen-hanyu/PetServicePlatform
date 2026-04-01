package com.petplatform.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SendVerifyCodeResponse(
        String phone,
        @JsonProperty("expires_in") long expiresIn,
        @JsonProperty("debug_code") String debugCode
) {
}
