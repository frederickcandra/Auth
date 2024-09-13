package com.jwt.auth.component;

import java.util.Objects;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jwt.auth.model.UserRedis;

import io.jsonwebtoken.io.SerializationException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserSerializer implements RedisSerializer<UserRedis> {
    private final ObjectMapper objectMapper;

    @Override
    public byte[] serialize(UserRedis userRedis) throws SerializationException {
        try {
            return objectMapper.writeValueAsBytes(userRedis); // Replace with actual serialization logic
        } catch (JsonProcessingException e) {
            throw new SerializationException("Error serializing User to JSON", e);
        }
    }

    @Override
    public UserRedis deserialize(byte[] bytes) throws SerializationException {
        try {
            if (Objects.nonNull(bytes)) {
                return objectMapper.readValue(bytes, UserRedis.class);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new SerializationException("Error deserializing JSON to User", e);
        }
    }
}