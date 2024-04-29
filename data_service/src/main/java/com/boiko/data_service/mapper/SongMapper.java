package com.boiko.data_service.mapper;

import com.boiko.data_service.dto.AuthorDTO;
import com.boiko.data_service.dto.SongDTO;
import com.boiko.data_service.model.Song;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SongMapper {
    private final AuthorMapper authorMapper;

    public SongDTO modelToDTO(Song model) {
        AuthorDTO[] authors = model
                .getAuthors()
                .stream()
                .map(authorMapper::modelToDTO)
                .toArray(AuthorDTO[]::new);

        return new SongDTO(
                model.getId(),
                model.getName(),
                authors,
                model.getAudio().getUrl(),
                model.getPicture().getUrl(),
                model.getAuditions(),
                model.getLikes()
        );
    }
}
