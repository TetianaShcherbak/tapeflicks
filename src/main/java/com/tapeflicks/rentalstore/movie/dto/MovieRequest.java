package com.tapeflicks.rentalstore.movie.dto;

import jakarta.validation.constraints.NotNull;

public record MovieRequest(
    @NotNull(message = "id must be provided") Long id,
    String title,
    String genre,
    Boolean available) {}
