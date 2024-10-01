package org.Roclh.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class JsonHandler {
    private final static ObjectMapper mapper = new ObjectMapper();

    public static String toJson(Object object) {
        try {
            String value = mapper.writeValueAsString(object);
            log.info("Parsing result: " + value);
            return value;
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new RuntimeException();
        }
    }

    public static <T> T toObject(String json, Class<T> clazz) throws JsonProcessingException {
        try {
            return mapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse json {} to class {}", json, clazz, e);
            throw e;
        }
    }

    public static List<String> toList(String json) {
        try {
            return mapper.readValue(json, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            return List.of();
        }
    }
}
