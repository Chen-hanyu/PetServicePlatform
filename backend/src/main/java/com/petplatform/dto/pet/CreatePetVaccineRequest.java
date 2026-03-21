package com.petplatform.dto.pet;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreatePetVaccineRequest(
        @JsonProperty("vaccine_name")
        @NotBlank(message = "疫苗名称不能为空")
        String vaccineName,

        @JsonProperty("vaccinated_at")
        @NotNull(message = "接种日期不能为空")
        LocalDate vaccinatedAt,

        @JsonProperty("next_due_at")
        LocalDate nextDueAt,

        String remark
) {
}
