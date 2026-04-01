package com.petplatform.dto.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record SaveAdoptionPetRequest(
        @NotBlank(message = "宠物名称不能为空")
        String name,
        @NotBlank(message = "宠物类型不能为空")
        String type,
        String breed,
        String gender,
        @JsonProperty("age_desc") String ageDesc,
        String city,
        @JsonProperty("health_status") String healthStatus,
        String personality,
        @JsonProperty("adoption_requirements") String adoptionRequirements,
        String story,
        @JsonProperty("cover_url") String coverUrl,
        @NotBlank(message = "宠物状态不能为空")
        String status
) {
}
