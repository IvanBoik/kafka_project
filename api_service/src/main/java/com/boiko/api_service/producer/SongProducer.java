package com.boiko.api_service.producer;

import com.boiko.api_service.dto.UploadSongDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SongProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendSongForUpload(UploadSongDTO songDTO) throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(songDTO);
        kafkaTemplate.send("songsTopic", json);
    }
}
