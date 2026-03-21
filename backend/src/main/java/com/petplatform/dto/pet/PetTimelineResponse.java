package com.petplatform.dto.pet;

import java.util.List;

public record PetTimelineResponse(
        PetProfileResponse pet,
        List<PetTimelineEventResponse> events
) {
}
