package com.petplatform.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.petplatform.entity.User;

public record UserProfileResponse(
        Long id,
        String role,
        String phone,
        String nickname,
        @JsonProperty("avatar_url") String avatarUrl,
        String gender,
        String bio,
        String status
) {

    public static UserProfileResponse from(User user) {
        return new UserProfileResponse(
                user.getId(),
                user.getRole(),
                user.getPhone(),
                user.getNickname(),
                user.getAvatarUrl(),
                user.getGender(),
                user.getBio(),
                user.getStatus()
        );
    }
}
