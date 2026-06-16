package com.petplatform.dto.message;

import com.fasterxml.jackson.annotation.JsonProperty;

public record HandleSupportMessageRequest(
        @JsonProperty("reply_content") String replyContent
) {
}
