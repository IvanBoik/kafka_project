package com.boiko.api_service.dto;

public record SongInAlbumDTO(
        Long[] authorsIDs,
        String name,
        int order,
        byte[] audio,
        String audioType
) {
}
