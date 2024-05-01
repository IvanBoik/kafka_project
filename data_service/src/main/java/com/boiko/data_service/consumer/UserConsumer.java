package com.boiko.data_service.consumer;

import com.boiko.data_service.dto.BecomeAuthorRequest;
import com.boiko.data_service.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserConsumer {
    private final UserService userService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "authorsTopic", groupId = "soundvibe")
    public void authorsListener(String json) throws JsonProcessingException {
        BecomeAuthorRequest request = objectMapper.readValue(json, BecomeAuthorRequest.class);
        userService.becomeAuthor(request);
        System.out.println(request);
    }
}
