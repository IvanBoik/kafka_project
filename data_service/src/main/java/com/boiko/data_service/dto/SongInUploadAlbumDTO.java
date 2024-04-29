package com.boiko.data_service.dto;

public record SongInUploadAlbumDTO(
        Long[] authorsIDs,
        String name,
        int order,
        byte[] audio,
        String audioType
) {
}
