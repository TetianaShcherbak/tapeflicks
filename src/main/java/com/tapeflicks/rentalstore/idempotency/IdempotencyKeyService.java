package com.tapeflicks.rentalstore.idempotency;

import com.tapeflicks.rentalstore.idempotency.dto.IdempotencyKeyResponse;
import com.tapeflicks.rentalstore.idempotency.exception.IdempotencyKeyNotFoundException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class IdempotencyKeyService {
  private final IdempotencyKeyRepository idempotencyKeyRepository;

  /**
   * Processes an idempotency key to ensure duplicate requests are handled safely.
   *
   * <p>On the first request with a given key, saves the key and response body to the database. On
   * subsequent requests with the same key, returns the cached response without modifying any stored
   * data, with a status reflecting whether the repeated request was identical or not:
   *
   * <ul>
   *   <li>{@code 208 Already Reported} — same key, same request body (safe retry)
   *   <li>{@code 409 Conflict} — same key, different request body (potentially malicious or buggy
   *       client)
   * </ul>
   *
   * @param idempotencyKey unique key provided by the client to identify the request
   * @param body serialized request body used to detect whether a repeated request is identical
   * @return {@link ResponseEntity} containing the cached {@link IdempotencyKeyResponse} and an
   *     appropriate HTTP status code
   */
  @Transactional
  public ResponseEntity<IdempotencyKeyResponse> saveIdempotencyKey(
      String idempotencyKey, String body) {
    Optional<IdempotencyKey> keyRecord = idempotencyKeyRepository.findById(idempotencyKey);
    if (keyRecord.isEmpty()) {
      return saveNewRecord(idempotencyKey, body);
    }

    int conflictCode = resolveConflictCause(keyRecord.get(), body).value();

    return prepareCustomResponse(keyRecord.get(), conflictCode);
  }

  public boolean isKeyUnique(String idempotencyKey) {
    return idempotencyKeyRepository.findById(idempotencyKey).isEmpty();
  }

  public HttpStatus resolveConflictCause(IdempotencyKey idempotencyKey, String body) {
    if (idempotencyKey.getResponseBody().equals(body)) {
      return HttpStatus.ALREADY_REPORTED;
    }

    return HttpStatus.CONFLICT;
  }

  private ResponseEntity<IdempotencyKeyResponse> saveNewRecord(String key, String body) {
    int responseStatus = HttpStatus.CREATED.value();

    IdempotencyKey idempotencyKey =
        IdempotencyKey.builder().key(key).responseStatus(responseStatus).responseBody(body).build();
    idempotencyKeyRepository.save(idempotencyKey);

    IdempotencyKeyResponse response =
        IdempotencyKeyResponse.builder()
            .idempotencyKey(key)
            .responseStatus(responseStatus)
            .responseBody(body)
            .createdAt(idempotencyKey.getCreatedAt())
            .build();

    return ResponseEntity.status(responseStatus).body(response);
  }

  public IdempotencyKey getIdempotencyKey(String idempotencyKey) {
    return idempotencyKeyRepository
        .findById(idempotencyKey)
        .orElseThrow(() -> new IdempotencyKeyNotFoundException(idempotencyKey));
  }

  private ResponseEntity<IdempotencyKeyResponse> prepareCustomResponse(
      IdempotencyKey idempotencyKey, int statusCode) {
    IdempotencyKeyResponse response =
        IdempotencyKeyResponse.builder()
            .idempotencyKey(idempotencyKey.getKey())
            .responseBody(idempotencyKey.getResponseBody())
            .createdAt(idempotencyKey.getCreatedAt())
            .build();

    return ResponseEntity.status(statusCode).body(response);
  }
}
