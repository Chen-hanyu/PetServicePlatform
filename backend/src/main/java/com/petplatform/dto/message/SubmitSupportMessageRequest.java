package com.petplatform.dto.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SubmitSupportMessageRequest(
        @NotBlank(message = "咨询内容不能为空")
        @Size(max = 1000, message = "咨询内容不能超过1000字")
        String content,

        @JsonProperty("source")
        @Size(max = 100, message = "来源不能超过100字")
        String source
) {
}
