package com.boiko.data_service.mapper;

import com.boiko.data_service.dto.AuthorLinkDTO;
import com.boiko.data_service.model.AuthorLink;
import org.springframework.stereotype.Component;

@Component
public class AuthorLinkMapper {
    public AuthorLink dtoToModel(AuthorLinkDTO dto) {
        return AuthorLink.builder()
                .serviceName(dto.serviceName())
                .url(dto.url())
                .build();
    }
}
