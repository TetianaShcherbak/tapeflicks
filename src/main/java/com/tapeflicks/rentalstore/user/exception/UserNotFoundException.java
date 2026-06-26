package com.tapeflicks.rentalstore.user.exception;

import com.tapeflicks.rentalstore.exception.ErrorCode;
import com.tapeflicks.rentalstore.exception.NotFoundException;

@SuppressWarnings("serial")
public class UserNotFoundException extends NotFoundException {
  public UserNotFoundException(Long userId) {
    super(ErrorCode.USER_NOT_FOUND, new String[] {userId.toString()});
  }
}
