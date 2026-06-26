package com.tapeflicks.rentalstore.rental.dto;

import java.time.Instant;
import lombok.Builder;

@Builder
public record RentalResponse(
    Long userId,
    Long movieId,
    String movieTitle,
    Instant rentedAt,
    Instant dueDate,
    Instant returnedAt) {}
