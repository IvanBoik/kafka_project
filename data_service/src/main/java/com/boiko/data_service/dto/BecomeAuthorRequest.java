package com.boiko.data_service.dto;

import java.util.List;

public record BecomeAuthorRequest(
        long userID,
        String biography,
        List<AuthorLinkDTO> links
) {
}
