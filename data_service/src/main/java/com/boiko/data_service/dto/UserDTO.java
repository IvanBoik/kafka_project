package com.boiko.data_service.dto;

import java.time.LocalDate;

public record UserDTO(
        String email,
        String password,
        String nickname,
        LocalDate birthday
) {
}
