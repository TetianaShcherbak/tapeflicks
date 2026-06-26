package com.tapeflicks.rentalstore.exception;

import com.tapeflicks.rentalstore.config.message.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

  private final MessageService messageService;

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<?> handle(NotFoundException ex) { // NOSONAR

    String message = messageService.getMessage(ex.getErrorCode(), (Object) ex.getArgs());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
  }
}
