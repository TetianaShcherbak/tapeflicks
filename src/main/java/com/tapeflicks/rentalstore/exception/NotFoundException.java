package com.tapeflicks.rentalstore.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@SuppressWarnings("serial")
public class NotFoundException extends RuntimeException {

  private final ErrorCode errorCode;
  private final String[] args;
}
