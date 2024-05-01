package com.boiko.data_service.consumer;

import com.boiko.data_service.dto.UploadSongDTO;
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
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "songsTopic", groupId = "soundvibe")
    public void songsListener(String json) throws IOException, UnsupportedAudioFileException {
        UploadSongDTO song = objectMapper.readValue(json, UploadSongDTO.class);
        if (song.dateOfPublication() == null) {
            songService.uploadSong(song);
        }
        else {
            songService.uploadSongAtDate(song);
        }
        System.out.println(song);
    }

    @KafkaListener(topics = "auditionsTopic", groupId = "soundvibe")
    public void auditionsListener(String songID) {
        songService.incrementAuditions(Long.parseLong(songID));
        System.out.println(songID);
    }
}
