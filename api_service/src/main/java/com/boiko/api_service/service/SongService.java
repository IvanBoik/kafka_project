package com.boiko.api_service.service;

import com.boiko.api_service.producer.SongProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SongService {
    private final RestTemplate restTemplate;
    private final SongProducer songProducer;

    public List<?> getTopSongs(int pageSize, int pageNumber) {
        return restTemplate.getForObject(
                "songs?page_size=%d&page_number=%d".formatted(pageSize, pageNumber),
                List.class
        );
    }

    public ResponseEntity<?> uploadSong(
            Long[] authorsIDs,
            MultipartFile audio,
            MultipartFile picture,
            LocalDateTime dateOfPublication
    ) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("authorIDs", authorsIDs);
        body.add("audio", audio);
        body.add("picture", picture);
        body.add("dateOfPublication", dateOfPublication);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        return restTemplate.postForEntity("songs/upload", requestEntity, String.class);
    }
}
