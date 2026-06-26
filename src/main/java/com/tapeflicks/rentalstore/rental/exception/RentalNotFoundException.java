package com.tapeflicks.rentalstore.rental.exception;

import com.tapeflicks.rentalstore.exception.ErrorCode;
import com.tapeflicks.rentalstore.exception.NotFoundException;

@SuppressWarnings("serial")
public class RentalNotFoundException extends NotFoundException {
  public RentalNotFoundException(Long rentalId) {
    super(ErrorCode.RENTAL_NOT_FOUND, new String[] {rentalId.toString()});
  }
}
