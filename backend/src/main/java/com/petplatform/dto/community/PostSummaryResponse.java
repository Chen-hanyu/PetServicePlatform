package com.petplatform.dto.community;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;

public record PostSummaryResponse(
        Long id,
        String title,
        String category,
        @JsonProperty("cover_url") String coverUrl,
        String excerpt,
        String status,
        @JsonProperty("like_count") int likeCount,
        @JsonProperty("favorite_count") int favoriteCount,
        @JsonProperty("comment_count") int commentCount,
        PostAuthorResponse author,
        List<String> tags,
        @JsonProperty("published_at") LocalDateTime publishedAt
) {
}
