package com.petplatform.dto.community;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.petplatform.entity.User;

public record PostAuthorResponse(
        Long id,
        String nickname,
        @JsonProperty("avatar_url") String avatarUrl
) {

    public static PostAuthorResponse from(User user) {
        return new PostAuthorResponse(user.getId(), user.getNickname(), user.getAvatarUrl());
    }
}
