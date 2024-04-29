package com.boiko.data_service.mapper;

import com.boiko.data_service.dto.AuthorDTO;
import com.boiko.data_service.model.Author;
import org.springframework.stereotype.Component;

@Component
public class AuthorMapper {
    public AuthorDTO modelToDTO(Author model) {
        return new AuthorDTO(
                model.getNickname(),
                model.getId(),
                model.getAvatar().getUrl()
        );
    }
}
