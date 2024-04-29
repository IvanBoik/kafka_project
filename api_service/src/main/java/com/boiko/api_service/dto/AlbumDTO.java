package com.boiko.api_service.dto;

public record AlbumDTO(
        String name,
        String pictureURL,
        Long id,
        AuthorDTO[] authors,
        SongDTO[] songs,
        long auditions,
        long likes
) {
}
