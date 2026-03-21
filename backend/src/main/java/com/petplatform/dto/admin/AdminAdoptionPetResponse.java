package com.petplatform.dto.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.petplatform.entity.AdoptionPet;

public record AdminAdoptionPetResponse(
        Long id,
        String name,
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
        String status
) {

    public static AdminAdoptionPetResponse from(AdoptionPet pet) {
        return new AdminAdoptionPetResponse(
                pet.getId(),
                pet.getName(),
                pet.getType(),
                pet.getBreed(),
                pet.getGender(),
                pet.getAgeDesc(),
                pet.getCity(),
                pet.getHealthStatus(),
                pet.getPersonality(),
                pet.getAdoptionRequirements(),
                pet.getStory(),
                pet.getCoverUrl(),
                pet.getStatus()
        );
    }
}
