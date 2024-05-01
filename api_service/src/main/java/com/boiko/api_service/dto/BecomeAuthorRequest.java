package com.boiko.api_service.dto;

import java.util.List;

public record BecomeAuthorRequest(
        Long userID,
        String biography,
        List<AuthorLinkDTO> links
) {
}
