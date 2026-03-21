package com.petplatform.dto.pet;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.petplatform.entity.Pet;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PetProfileResponse(
        Long id,
        String name,
        String type,
        String breed,
        String gender,
        LocalDate birthday,
        BigDecimal weight,
        @JsonProperty("avatar_url") String avatarUrl,
        String description
) {

    public static PetProfileResponse from(Pet pet) {
        return new PetProfileResponse(
                pet.getId(),
                pet.getName(),
                pet.getType(),
                pet.getBreed(),
                pet.getGender(),
                pet.getBirthday(),
                pet.getWeight(),
                pet.getAvatarUrl(),
                pet.getDescription()
        );
    }
}
