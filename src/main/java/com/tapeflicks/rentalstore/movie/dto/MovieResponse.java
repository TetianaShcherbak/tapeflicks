package com.tapeflicks.rentalstore.movie.dto;

import lombok.Builder;

@Builder
public record MovieResponse(Long id, String title, String genre, boolean available) {}
