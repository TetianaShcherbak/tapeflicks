package com.tapeflicks.rentalstore.idempotency.dto;

import java.time.Instant;
import lombok.Builder;

@Builder
public record IdempotencyKeyResponse(
    String idempotencyKey, Instant createdAt, int responseStatus, String responseBody) {}
