package com.petplatform.dto.admin;

import com.petplatform.entity.Tag;

public record AdminTagResponse(
        Long id,
        String name,
        String type,
        String status,
        int sort
) {

    public static AdminTagResponse from(Tag tag) {
        return new AdminTagResponse(
                tag.getId(),
                tag.getName(),
                tag.getType(),
                tag.getStatus(),
                tag.getSort() == null ? 0 : tag.getSort()
        );
    }
}
