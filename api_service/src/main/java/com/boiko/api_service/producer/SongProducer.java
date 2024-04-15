package com.boiko.api_service.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class SongProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendSongForUpload(
            Long[] authorsIDs,
            MultipartFile audio,
            MultipartFile picture,
            LocalDateTime dateOfPublication
    ) {

    }
}
