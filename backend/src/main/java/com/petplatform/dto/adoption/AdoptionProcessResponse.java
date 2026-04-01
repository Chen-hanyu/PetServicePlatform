package com.petplatform.dto.adoption;

import java.util.List;

public record AdoptionProcessResponse(
        List<String> steps,
        List<String> notes
) {
}
