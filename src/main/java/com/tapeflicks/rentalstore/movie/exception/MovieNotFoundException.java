package com.tapeflicks.rentalstore.movie.exception;

import com.tapeflicks.rentalstore.exception.ErrorCode;
import com.tapeflicks.rentalstore.exception.NotFoundException;

@SuppressWarnings("serial")
public class MovieNotFoundException extends NotFoundException {
  public MovieNotFoundException(Long movieId) {
    super(ErrorCode.MOVIE_NOT_FOUND, new String[] {movieId.toString()});
  }
}
