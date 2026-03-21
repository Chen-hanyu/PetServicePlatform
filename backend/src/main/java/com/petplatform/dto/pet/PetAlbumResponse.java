package com.petplatform.dto.pet;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.petplatform.entity.PetAlbum;

public record PetAlbumResponse(
        Long id,
        @JsonProperty("image_url") String imageUrl,
        String caption
) {

    public static PetAlbumResponse from(PetAlbum album) {
        return new PetAlbumResponse(album.getId(), album.getImageUrl(), album.getCaption());
    }
}
