package com.petplatform.dto.adoption;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.petplatform.entity.AdoptionPet;

public record AdoptionPetSummaryResponse(
        Long id,
        String name,
        String type,
        String breed,
        String gender,
        @JsonProperty("age_desc") String ageDesc,
        String city,
        @JsonProperty("health_status") String healthStatus,
        String status,
        @JsonProperty("cover_url") String coverUrl
) {

    public static AdoptionPetSummaryResponse from(AdoptionPet pet) {
        return new AdoptionPetSummaryResponse(
                pet.getId(),
                pet.getName(),
                pet.getType(),
                pet.getBreed(),
                pet.getGender(),
                pet.getAgeDesc(),
                pet.getCity(),
                pet.getHealthStatus(),
                pet.getStatus(),
                pet.getCoverUrl()
        );
    }
}
