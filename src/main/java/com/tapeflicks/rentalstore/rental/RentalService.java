package com.tapeflicks.rentalstore.rental;

import com.tapeflicks.rentalstore.idempotency.IdempotencyKey;
import com.tapeflicks.rentalstore.idempotency.IdempotencyKeyService;
import com.tapeflicks.rentalstore.movie.Movie;
import com.tapeflicks.rentalstore.movie.MovieService;
import com.tapeflicks.rentalstore.rental.dto.RentalRequest;
import com.tapeflicks.rentalstore.rental.dto.RentalResponse;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import com.tapeflicks.rentalstore.rental.exception.MovieIsNoLongerAvailableException;
import com.tapeflicks.rentalstore.user.User;
import com.tapeflicks.rentalstore.user.UserService;
import com.tapeflicks.rentalstore.util.JsonProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RentalService {
    private final RentalRepository rentalRepository;
    private final IdempotencyKeyService idempotencyKeyService;
    private final MovieService movieService;
    private final UserService userService;

    private final JsonProcessor jsonProcessor;

    @Value("${rentalstore.rental.period-days:7}")
    private int rentalPeriodDaysDefault;

    /**
     * Rents a movie to a user.
     *
     * <p>Availability check, movie flag update, and rental record creation happen in one transaction
     * since they are core business logic that must be atomic.
     *
     * <p>If the idempotency key was already used, returns the cached response without modifying any
     * data. The status reflects whether the repeated request was identical or conflicting:
     *
     * <ul>
     *   <li>{@code 208 Already Reported} — same key, same request body (safe retry)
     *   <li>{@code 409 Conflict} — same key, different request body
     * </ul>
     *
     * @param idempotencyKey unique key provided by the client to prevent duplicate rentals
     * @param request contains userId, movieId, and optional rental period in days
     * @return {@code 201 Created} with {@link RentalResponse} on first successful request, or a
     *     cached response with {@code 208}/{@code 409} on duplicate requests
     * @throws MovieIsNoLongerAvailableException if the requested movie is not available for rent
     */
    @Transactional
    public ResponseEntity<RentalResponse> rentMovie(String idempotencyKey, RentalRequest request) {

        if (!idempotencyKeyService.isKeyUnique(idempotencyKey)) {
            IdempotencyKey keyRecord = idempotencyKeyService.getIdempotencyKey(idempotencyKey);
            String cachedResponse = keyRecord.getResponseBody();
            HttpStatus status =
                    idempotencyKeyService.resolveConflictCause(keyRecord, keyRecord.getResponseBody());

            return ResponseEntity.status(status)
                    .body(jsonProcessor.readValue(cachedResponse, RentalResponse.class));
        }

        Long movieId = request.movieId();
        if (!movieService.isMovieAvailable(movieId)) {
            throw new MovieIsNoLongerAvailableException(movieId);
        }

        Movie movie = movieService.getMovie(movieId);
        User user = userService.getUser(request.userId());

        int rentalPeriodDays =
                request.rentalPeriodDays() > 0 ? request.rentalPeriodDays() : rentalPeriodDaysDefault;

        Instant now = Instant.now();
        Instant dueDate = now.plus(Duration.ofDays(rentalPeriodDays));

        Rental rental = Rental.builder().user(user).movie(movie).rentedAt(now).dueDate(dueDate).build();
        rentalRepository.save(rental);

        movie.setAvailable(false);
        movieService.updateMovie(movie);

        RentalResponse response =
                RentalResponse.builder()
                        .userId(user.getId())
                        .movieId(movieId)
                        .movieTitle(movie.getTitle())
                        .rentedAt(now)
                        .dueDate(dueDate)
                        .build();

        idempotencyKeyService.saveIdempotencyKey(
                idempotencyKey, jsonProcessor.writeValueAsString(response, RentalResponse.class));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }



    @Transactional
  public RentalResponse returnMovie(Long rentalId) {
      return null;
  }


  @Transactional(readOnly = true)
  public List<RentalResponse> findAllRentedMoviesByUserId(Long userId) {
    return null;
  }

  @Transactional(readOnly = true)
  public List<RentalResponse> findAllCurrentlyRentedMoviesByUserId(Long userId) {
      return null;
  }

  @Transactional(readOnly = true)
  public List<RentalResponse> findAllReturnedMoviesByUserId(Long userId) {
      return null;
  }
}
