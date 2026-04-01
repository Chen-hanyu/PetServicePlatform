package com.petplatform.dto.community;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreatePostRequest(
        @NotBlank(message = "标题不能为空")
        @Size(max = 100, message = "标题长度不能超过100个字符")
        String title,

        @NotBlank(message = "正文不能为空")
        String content,

        @NotBlank(message = "分类不能为空")
        String category,

        List<String> images,

        @JsonProperty("tag_ids") List<Long> tagIds
) {
}
