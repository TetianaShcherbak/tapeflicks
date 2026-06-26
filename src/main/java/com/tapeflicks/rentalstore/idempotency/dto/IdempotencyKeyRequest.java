package com.tapeflicks.rentalstore.idempotency.dto;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record IdempotencyKeyRequest(
    @NotNull(message = "IdempotencyKey value must be provided") String idempotencyKey,
    @NotNull(message = "created_at must be provided") Instant createdAt) {}
