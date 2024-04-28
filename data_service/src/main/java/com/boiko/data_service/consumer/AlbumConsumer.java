package com.boiko.data_service.consumer;

import com.boiko.data_service.dto.AlbumDTO;
import com.boiko.data_service.service.AlbumService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AlbumConsumer {
    private final AlbumService albumService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "albumsTopic", groupId = "soundvibe")
    void songsListener(String json) throws IOException, UnsupportedAudioFileException {
        AlbumDTO album = objectMapper.readValue(json, AlbumDTO.class);
        if (album.dateOfPublication() == null) {
            albumService.uploadAlbum(album);
        }
        else {
            albumService.uploadAlbumAtDate(album);
        }
        System.out.println(album);
    }
}
