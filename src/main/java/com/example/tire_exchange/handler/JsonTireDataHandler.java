package com.example.tire_exchange.handler;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class JsonTireDataHandler implements TireDataHandler {

    private final ObjectMapper objectMapper;

    public JsonTireDataHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String parseTireData(String data) {
        // Parse JSON data
        try {
            Object json = objectMapper.readValue(data, Object.class);
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JSON", e);
        }
    }
}
