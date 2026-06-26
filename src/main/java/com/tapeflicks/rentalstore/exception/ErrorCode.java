package com.tapeflicks.rentalstore.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    MOVIE_NOT_AVAILABLE,
    MOVIE_NOT_FOUND,
    RENTAL_NOT_FOUND,
    USER_NOT_FOUND;
}
