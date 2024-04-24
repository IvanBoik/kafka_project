package com.boiko.api_service.service;

import com.boiko.api_service.dto.SongDTO;
import com.boiko.api_service.producer.SongProducer;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    public void uploadSong(Long[] authorsIDs, MultipartFile audio, MultipartFile picture) throws IOException {
        uploadSongAtDate(authorsIDs, audio, picture, null);
    }

    public void uploadSongAtDate(
            Long[] authorsIDs,
            MultipartFile audio,
            MultipartFile picture,
            LocalDateTime dateOfPublication
    ) throws IOException {

        String name = FilenameUtils.removeExtension(audio.getOriginalFilename());
        byte[] audioBytes = audio.getBytes();
        String audioType = audio.getContentType();
        byte[] pictureBytes = picture.getBytes();
        String pictureType = picture.getContentType();

        SongDTO songDTO = new SongDTO(
                authorsIDs,
                name,
                audioBytes,
                audioType,
                pictureBytes,
                pictureType,
                dateOfPublication
        );
        songProducer.sendSongForUpload(songDTO);
    }
}
