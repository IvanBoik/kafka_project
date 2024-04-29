package com.boiko.api_service.dto;

public record AuthorDTO(
        String name,
        Long id,
        String avatarURL
) {
}
