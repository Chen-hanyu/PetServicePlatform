package com.petplatform.dto.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record ReviewAdoptionApplicationRequest(
        @NotBlank(message = "审核状态不能为空")
        String status,
        @JsonProperty("review_remark")
        String reviewRemark
) {
}
