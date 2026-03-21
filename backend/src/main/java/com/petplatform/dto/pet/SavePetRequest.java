package com.petplatform.dto.pet;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SavePetRequest(
        @NotBlank(message = "宠物名称不能为空")
        @Size(max = 50, message = "宠物名称长度不能超过50个字符")
        String name,

        @NotBlank(message = "宠物类型不能为空")
        String type,

        String breed,
        String gender,
        LocalDate birthday,
        BigDecimal weight,
        @JsonProperty("avatar_url") String avatarUrl,
        String description
) {
}
