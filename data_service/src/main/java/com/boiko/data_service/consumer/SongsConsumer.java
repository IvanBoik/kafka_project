package com.boiko.data_service.consumer;

import com.boiko.data_service.dto.SongDTO;
import com.boiko.data_service.service.SongService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SongsConsumer {
    private final SongService songService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "songsTopic", groupId = "soundvibe")
    void songsListener(String json) throws IOException, UnsupportedAudioFileException {
        SongDTO song = objectMapper.readValue(json, SongDTO.class);
        if (song.dateOfPublication() == null) {
            songService.uploadSong(song);
        }
        else {
            songService.uploadSongAtDate(song);
        }
        System.out.println(song);
    }
}
