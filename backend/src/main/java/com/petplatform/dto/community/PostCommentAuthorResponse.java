package com.petplatform.dto.community;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.petplatform.entity.User;

public record PostCommentAuthorResponse(
        Long id,
        String nickname,
        @JsonProperty("avatar_url") String avatarUrl
) {

    public static PostCommentAuthorResponse from(User user) {
        return new PostCommentAuthorResponse(user.getId(), user.getNickname(), user.getAvatarUrl());
    }
}
