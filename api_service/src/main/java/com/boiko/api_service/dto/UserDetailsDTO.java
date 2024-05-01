package com.boiko.api_service.dto;

import java.time.LocalDate;

public record UserDetailsDTO(
        Long id,
        String nickname,
        String email,
        LocalDate birthday,
        String avatarURL
) {
}
