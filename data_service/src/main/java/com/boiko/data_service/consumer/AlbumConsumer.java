package com.boiko.data_service.consumer;

import com.boiko.data_service.dto.UploadAlbumDTO;
import com.boiko.data_service.service.AlbumService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Component;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AlbumConsumer {
    private final AlbumService albumService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "albumsTopic", groupId = "soundvibe")
    public void songsListener(String json) throws IOException, UnsupportedAudioFileException {
        UploadAlbumDTO album = objectMapper.readValue(json, UploadAlbumDTO.class);
        if (album.dateOfPublication() == null) {
            albumService.uploadAlbum(album);
        }
        else {
            albumService.uploadAlbumAtDate(album);
        }
        System.out.println(album);
    }

    @KafkaListener(topics = "auditionsTopic", groupId = "soundvibe")
    public void auditionsListener(String songID) {
        albumService.incrementAuditions(Long.parseLong(songID));
        System.out.println("AlbumService: " + songID);
    }
}
