package com.tapeflicks.rentalstore.rental.dto;

import jakarta.validation.constraints.NotNull;

public record RentalRequest(
    @NotNull(message = "userId must be provided") Long userId,
    @NotNull(message = "movieId must be provided") Long movieId,
    int rentalPeriodDays) {}
