package com.boiko.data_service.dto;

public record AuthorDTO(
        String name,
        Long id,
        String avatarURL
) {
}
