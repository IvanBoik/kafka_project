package com.boiko.api_service.service;

import com.boiko.api_service.dto.BecomeAuthorRequest;
import com.boiko.api_service.dto.UserDTO;
import com.boiko.api_service.dto.UserDetailsDTO;
import com.boiko.api_service.producer.UserProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class UserService {
    private final RestTemplate restTemplate;
    private final UserProducer userProducer;

    public UserDetailsDTO findUserByID(Long id) {
        return restTemplate.getForObject("/users/" + id, UserDetailsDTO.class);
    }

    public Long signUp(UserDTO userDTO) {
        return restTemplate.postForObject("/users/signup", userDTO, Long.class);
    }

    public Long logIn(String email, String password) {
        return restTemplate.getForObject(
                "/users/login?email=%s&password=%s".formatted(email, password),
                Long.class
        );
    }

    public void becomeAuthor(BecomeAuthorRequest request) throws JsonProcessingException {
        userProducer.becameAuthor(request);
    }

    public Long toggleLikeSong(Long userID, Long songID) {
        return restTemplate.getForObject(
                "/like/song?userID=%d&songID=%d".formatted(userID, songID),
                Long.class
        );
    }

    public Long toggleLikeAlbum(Long userID, Long albumID) {
        return restTemplate.getForObject(
                "/like/song?userID=%d&albumID=%d".formatted(userID, albumID),
                Long.class
        );
    }

    public Long toggleLikeAuthor(Long userID, Long authorID) {
        return restTemplate.getForObject(
                "/like/song?userID=%d&authorID=%d".formatted(userID, authorID),
                Long.class
        );
    }
}
