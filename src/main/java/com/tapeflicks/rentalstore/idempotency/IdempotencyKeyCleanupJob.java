package com.tapeflicks.rentalstore.idempotency;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class IdempotencyKeyCleanupJob {

  private final IdempotencyKeyRepository idempotencyRepository;

  public IdempotencyKeyCleanupJob(IdempotencyKeyRepository idempotencyRepository) {
    this.idempotencyRepository = idempotencyRepository;
  }

  @Scheduled(cron = "0 0 3 * * *") // Once a day, at 3 AM
  public void cleanupExpiredKeys() {
    Instant cutoff = Instant.now().minus(24, ChronoUnit.HOURS);
    int deleted = idempotencyRepository.deleteByCreatedAtBefore(cutoff);
    //        log.info("Cleaned up {} expired idempotency keys older than {}", deleted, cutoff);
  }
}
