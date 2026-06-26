package com.tapeflicks.rentalstore.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tapeflicks.rentalstore.config.message.MessageService;
import com.tapeflicks.rentalstore.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JsonProcessor {

  private final ObjectMapper objectMapper;
  private final MessageService messageService;

  public <T> String writeValueAsString(Object jsonObject, Class<T> targetType) {
    try {
      return objectMapper.writeValueAsString(jsonObject);
    } catch (JsonProcessingException e) {
      String msg = messageService.getMessage(ErrorCode.SERIALIZATION_FAILED, targetType);
      throw new IllegalStateException(msg, e);
    }
  }

  public <T> T readValue(String json, Class<T> targetType) {
    try {
      return objectMapper.readValue(json, targetType);
    } catch (JsonProcessingException e) {
      String msg = messageService.getMessage(ErrorCode.DESERIALIZATION_FAILED, targetType, json);
      throw new IllegalStateException(msg, e);
    }
  }
}
