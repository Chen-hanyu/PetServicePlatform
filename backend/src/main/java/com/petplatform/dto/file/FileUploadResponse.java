package com.petplatform.dto.file;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FileUploadResponse(
        String url,
        @JsonProperty("file_name") String fileName,
        @JsonProperty("content_type") String contentType,
        long size
) {
}
