package com.boiko.api_service.producer;

import com.boiko.api_service.dto.UploadAlbumDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AlbumProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendAlbumForUpload(UploadAlbumDTO albumDTO) throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(albumDTO);
        String key = UUID.randomUUID().toString();
        kafkaTemplate.send("albumsTopic", key, json);
    }
}
