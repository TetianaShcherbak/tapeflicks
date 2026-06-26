package com.tapeflicks.rentalstore.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
  DESERIALIZATION_FAILED,
  IDEMPOTENCY_KEY_NOT_FOUND,
  MOVIE_NOT_AVAILABLE,
  MOVIE_NOT_FOUND,
  RENTAL_NOT_FOUND,
  SERIALIZATION_FAILED,
  USER_NOT_FOUND;
}
