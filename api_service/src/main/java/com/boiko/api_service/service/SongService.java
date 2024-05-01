package com.boiko.api_service.service;

import com.boiko.api_service.dto.SongDTO;
import com.boiko.api_service.dto.UploadSongDTO;
import com.boiko.api_service.producer.SongProducer;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SongService {
    private final RestTemplate restTemplate;
    private final SongProducer songProducer;

    public SongDTO[] getTopSongsByLikes(int pageSize, int pageNumber) {
        return restTemplate.getForObject(
                "/songs/by_likes?page_size=%d&page_number=%d".formatted(pageSize, pageNumber),
                SongDTO[].class
        );
    }

    public SongDTO[] getTopSongsByAuditions(int pageSize, int pageNumber) {
        return restTemplate.getForObject(
                "/songs/by_auditions?page_size=%d&page_number=%d".formatted(pageSize, pageNumber),
                SongDTO[].class
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

        UploadSongDTO songDTO = new UploadSongDTO(
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

    public void incrementSongAuditions(Long songID) {
        songProducer.incrementSongAuditions(songID);
    }

    public Long getSongAuditions(Long songID) {
        return restTemplate.getForObject(
                "/songs/%d/auditions".formatted(songID),
                Long.class
        );
    }
}
