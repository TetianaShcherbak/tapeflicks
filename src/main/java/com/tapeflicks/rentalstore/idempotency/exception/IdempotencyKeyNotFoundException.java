package com.tapeflicks.rentalstore.idempotency.exception;

import com.tapeflicks.rentalstore.exception.ErrorCode;
import com.tapeflicks.rentalstore.exception.NotFoundException;

@SuppressWarnings("serial")
public class IdempotencyKeyNotFoundException extends NotFoundException {
  public IdempotencyKeyNotFoundException(String key) {
    super(ErrorCode.IDEMPOTENCY_KEY_NOT_FOUND, new String[] {key});
  }
}
