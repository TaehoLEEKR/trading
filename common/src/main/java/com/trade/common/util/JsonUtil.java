package com.trade.common.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;

public class JsonUtil {

    private static final JsonUtil INSTANCE = new JsonUtil();

    private final ObjectMapper objectMapper;

    private JsonUtil() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static JsonUtil getInstance() {
        return INSTANCE;
    }


    public <T> String encodeToJson(T object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (IOException e) {
            throw new RuntimeException("JSON 직렬화 중 오류 발생", e);
        }
    }


    public <T> T decodeFromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (IOException e) {
            throw new RuntimeException("JSON 역직렬화 중 오류 발생", e);
        }
    }
}