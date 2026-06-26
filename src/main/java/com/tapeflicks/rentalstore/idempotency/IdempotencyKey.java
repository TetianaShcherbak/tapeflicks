package com.tapeflicks.rentalstore.idempotency;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "idempotency_keys")
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class IdempotencyKey {

  @Id
  @Column(name = "key", nullable = false, updatable = false)
  private String key;

  @Column(name = "response_status", nullable = false)
  private int responseStatus;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "response_body", nullable = false, columnDefinition = "jsonb")
  private String responseBody;

  @Column(name = "created_at", updatable = false)
  private final Instant createdAt = Instant.now();
}
