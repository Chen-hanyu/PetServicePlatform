package com.petplatform.dto.pet;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.petplatform.entity.PetVaccine;

import java.time.LocalDate;

public record PetVaccineResponse(
        Long id,
        @JsonProperty("vaccine_name") String vaccineName,
        @JsonProperty("vaccinated_at") LocalDate vaccinatedAt,
        @JsonProperty("next_due_at") LocalDate nextDueAt,
        String remark
) {

    public static PetVaccineResponse from(PetVaccine vaccine) {
        return new PetVaccineResponse(
                vaccine.getId(),
                vaccine.getVaccineName(),
                vaccine.getVaccinatedAt(),
                vaccine.getNextDueAt(),
                vaccine.getRemark()
        );
    }
}
