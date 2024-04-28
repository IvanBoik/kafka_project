package com.boiko.api_service.dto;

import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.List;

public record AlbumDTO(
        String name,
        List<SongInAlbumDTO> songs,
        Long[] authorsIDs,
        byte[] picture,
        String pictureType,
        @Nullable
        LocalDateTime dateOfPublication
) {
}
