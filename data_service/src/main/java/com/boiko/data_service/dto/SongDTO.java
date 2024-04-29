package com.boiko.data_service.dto;

public record SongDTO(
        Long id,
        String name,
        AuthorDTO[] authors,
        String audioURL,
        String pictureURL,
        long auditions,
        long likes
) {
}
