package com.boiko.data_service.mapper;

import com.boiko.data_service.dto.AlbumDTO;
import com.boiko.data_service.dto.AuthorDTO;
import com.boiko.data_service.dto.SongDTO;
import com.boiko.data_service.model.Album;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AlbumMapper {
    private final SongMapper songMapper;
    private final AuthorMapper authorMapper;

    public AlbumDTO modelToDTO(Album model) {
        AuthorDTO[] authors = model
                .getAuthors()
                .stream()
                .map(authorMapper::modelToDTO)
                .toArray(AuthorDTO[]::new);

        SongDTO[] songs = model
                .getSongs()
                .stream()
                .map(songMapper::modelToDTO)
                .toArray(SongDTO[]::new);

        return new AlbumDTO(
                model.getName(),
                model.getPicture().getUrl(),
                model.getId(),
                authors,
                songs,
                model.getAuditions(),
                model.getLikes()
        );
    }
}
