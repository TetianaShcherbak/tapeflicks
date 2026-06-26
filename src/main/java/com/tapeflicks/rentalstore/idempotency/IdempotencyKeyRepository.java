package com.tapeflicks.rentalstore.idempotency;

import java.time.Instant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdempotencyKeyRepository extends JpaRepository<IdempotencyKey, String> {
  int deleteByCreatedAtBefore(Instant cutoff);
}
