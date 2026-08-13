package ru.bank.notification_service.infrastructure.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class JsonUtil {

    private final ObjectMapper objectMapper;

    public String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception ex) {
            log.warn("Ошибка сериализации в Json: {}", ex.getMessage());
            throw new RuntimeException("Ошибка сериализации в Json", ex);
        }
    }

}
