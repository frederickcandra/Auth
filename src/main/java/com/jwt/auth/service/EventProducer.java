package com.jwt.auth.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class EventProducer {

    private static final String TOPIC = "user-events";

    private final KafkaTemplate<String, String> kafkaTemplate;

    public EventProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendLoginEvent(String username) {
        String eventMessage = "User " + username + " has logged in.";
        kafkaTemplate.send(TOPIC, eventMessage);
    }

    public void sendFetchEvent(String username, String apiUrl) {
        String eventMessage = "User " + username + " fetched data from: " + apiUrl;
        kafkaTemplate.send(TOPIC, eventMessage);
    }
}