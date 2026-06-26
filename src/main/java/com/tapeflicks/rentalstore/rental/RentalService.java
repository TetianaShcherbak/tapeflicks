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
import com.tapeflicks.rentalstore.rental.exception.RentalNotFoundException;
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

    /**
     * Processes a movie return for the given rental.
     *
     * <p>Sets the return timestamp and marks the movie as available again. If the movie was already
     * returned, returns the current rental state without modification.
     *
     * @param rentalId ID of the rental record to close
     * @return {@link RentalResponse} reflecting the current state of the rental
     * @throws RentalNotFoundException if no rental exists with the given ID
     */
    @Transactional
    public RentalResponse returnMovie(Long rentalId) {
        Rental rental =
                rentalRepository
                        .findById(rentalId)
                        .orElseThrow(() -> new RentalNotFoundException(rentalId));
        if (rental.getReturnedAt() != null) {
            return prepareResponse(rental); // movie is already returned - return current state
        }
        rental.setReturnedAt(Instant.now());
        Movie movie = rental.getMovie();
        movie.setAvailable(true);
        movieService.updateMovie(movie);
        return prepareResponse(rental);
    }



    /**
     * Retrieves the full rental history for a user, including both active and returned rentals.
     *
     * @param userId ID of the user whose rentals to fetch
     * @return list of {@link RentalResponse} for all rentals belonging to the user, or an empty list
     *     if none exist
     */
    @Transactional(readOnly = true)
    public List<RentalResponse> findAllRentedMoviesByUserId(Long userId) {
        List<Rental> rentals = rentalRepository.findAllRentalsByUserId(userId);
        return prepareResponse(rentals);
    }

    /**
     * Retrieves all active (not yet returned) rentals for a user.
     *
     * @param userId ID of the user whose active rentals to fetch
     * @return list of {@link RentalResponse} for rentals where {@code returnedAt} is null, or an
     *     empty list if none exist
     */
    @Transactional(readOnly = true)
    public List<RentalResponse> findAllCurrentlyRentedMoviesByUserId(Long userId) {
        List<Rental> currentRentals = rentalRepository.findAllCurrentRentalsByUserId(userId);
        return prepareResponse(currentRentals);
    }

    /**
     * Retrieves all completed (returned) rentals for a user.
     *
     * @param userId ID of the user whose rental history to fetch
     * @return list of {@link RentalResponse} for rentals where {@code returnedAt} is set, or an empty
     *     list if none exist
     */
    @Transactional(readOnly = true)
    public List<RentalResponse> findAllReturnedMoviesByUserId(Long userId) {
        List<Rental> pastRentals = rentalRepository.findAllReturnedMoviesByUserId(userId);
        return prepareResponse(pastRentals);
    }

    private List<RentalResponse> prepareResponse(List<Rental> rentals) {
        return rentals.stream().map(this::prepareResponse).toList();
    }

    private RentalResponse prepareResponse(Rental rental) {
        Movie movie = rental.getMovie();
        return RentalResponse.builder()
                .userId(rental.getUser().getId())
                .movieId(movie.getId())
                .movieTitle(movie.getTitle())
                .rentedAt(rental.getRentedAt())
                .dueDate(rental.getDueDate())
                .returnedAt(rental.getReturnedAt())
                .build();
    }
}
