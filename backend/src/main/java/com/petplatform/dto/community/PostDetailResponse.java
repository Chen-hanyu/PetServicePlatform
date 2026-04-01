package com.petplatform.dto.community;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;

public record PostDetailResponse(
        Long id,
        String title,
        String content,
        List<String> images,
        String category,
        List<String> tags,
        PostAuthorResponse author,
        @JsonProperty("like_count") int likeCount,
        @JsonProperty("favorite_count") int favoriteCount,
        @JsonProperty("comment_count") int commentCount,
        @JsonProperty("is_liked") boolean isLiked,
        @JsonProperty("is_favorited") boolean isFavorited,
        @JsonProperty("published_at") LocalDateTime publishedAt
) {
}
