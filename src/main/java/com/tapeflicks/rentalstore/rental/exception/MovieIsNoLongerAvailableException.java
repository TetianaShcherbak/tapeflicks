package com.tapeflicks.rentalstore.rental.exception;

import com.tapeflicks.rentalstore.exception.ErrorCode;
import com.tapeflicks.rentalstore.exception.NotFoundException;

@SuppressWarnings("serial")
public class MovieIsNoLongerAvailableException extends NotFoundException {
  public MovieIsNoLongerAvailableException(Long movieId) {
    super(ErrorCode.MOVIE_NOT_AVAILABLE, new String[] {movieId.toString()});
  }
}
