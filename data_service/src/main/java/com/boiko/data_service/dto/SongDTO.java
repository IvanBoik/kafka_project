package com.boiko.data_service.dto;

import org.springframework.lang.Nullable;

import java.time.LocalDateTime;

public record SongDTO(
        Long[] authorsIDs,
        String name,
        byte[] audio,
        String audioType,
        byte[] picture,
        String pictureType,
        @Nullable
        LocalDateTime dateOfPublication
) {
}
