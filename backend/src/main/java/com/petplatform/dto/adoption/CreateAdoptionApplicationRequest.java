package com.petplatform.dto.adoption;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record CreateAdoptionApplicationRequest(
        @JsonProperty("pet_id")
        @NotNull(message = "宠物ID不能为空")
        Long petId,

        @JsonProperty("contact_phone")
        @NotBlank(message = "联系电话不能为空")
        @Pattern(regexp = "^1\\d{10}$", message = "手机号格式不正确")
        String contactPhone,

        @JsonProperty("experience_desc")
        @NotBlank(message = "养宠经验不能为空")
        String experienceDesc,

        @JsonProperty("living_condition_desc")
        @NotBlank(message = "居住情况不能为空")
        String livingConditionDesc
) {
}
