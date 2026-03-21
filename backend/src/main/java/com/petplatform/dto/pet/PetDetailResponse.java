package com.petplatform.dto.pet;

import java.util.List;

public record PetDetailResponse(
        PetProfileResponse pet,
        List<PetVaccineResponse> vaccines,
        List<PetWeightResponse> weights,
        List<PetAlbumResponse> albums
) {
}
