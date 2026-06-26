package com.tapeflicks.rentalstore.rental;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tapeflicks.rentalstore.idempotency.IdempotencyKey;
import com.tapeflicks.rentalstore.idempotency.IdempotencyKeyService;
import com.tapeflicks.rentalstore.movie.Movie;
import com.tapeflicks.rentalstore.movie.MovieService;
import com.tapeflicks.rentalstore.rental.dto.RentalRequest;
import com.tapeflicks.rentalstore.rental.dto.RentalResponse;
import com.tapeflicks.rentalstore.rental.exception.MovieIsNoLongerAvailableException;
import com.tapeflicks.rentalstore.user.User;
import com.tapeflicks.rentalstore.user.UserService;
import com.tapeflicks.rentalstore.util.JsonProcessor;
import java.time.Duration;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class RentalServiceTest {

  @Mock private RentalRepository rentalRepository;
  @Mock private IdempotencyKeyService idempotencyKeyService;
  @Mock private MovieService movieService;
  @Mock private UserService userService;
  @Mock private JsonProcessor jsonProcessor;

  @InjectMocks private RentalService rentalService;

  private static final String IDEMPOTENCY_KEY = "3f29a1c4-8e77-4b3a-9c21-7d4e9a1b2f0e";

  private static final Long USER_ID = 1L;
  private static final String USER_NAME = "John Doe";
  private static final String USER_EMAIL = "john.doe@example.com";

  private static final Long MOVIE_ID = 3L;
  private static final String MOVIE_TITLE = "Star Wars: A New Hope";
  private static final String MOVIE_GENRE = "Space Opera and Science Fantasy";

  private User user;
  private Movie movie;
  private RentalRequest request;

  @BeforeEach
  void setUp() {
    // Field default normally injected via @Value; set explicitly since no Spring context here.
    ReflectionTestUtils.setField(rentalService, "rentalPeriodDaysDefault", 7);

    user = User.builder().id(USER_ID).name(USER_NAME).email(USER_EMAIL).build();

    movie =
        Movie.builder().id(MOVIE_ID).title(MOVIE_TITLE).genre(MOVIE_GENRE).available(true).build();

    request = new RentalRequest(USER_ID, MOVIE_ID, 0);
  }

  // ---------- rentMovie ----------

  @Test
  void rentMovie_createsRental_whenKeyIsUniqueAndMovieAvailable() {
    when(idempotencyKeyService.isKeyUnique(IDEMPOTENCY_KEY)).thenReturn(true);
    when(movieService.isMovieAvailable(MOVIE_ID)).thenReturn(true);
    when(movieService.getMovie(MOVIE_ID)).thenReturn(movie);
    when(userService.getUser(USER_ID)).thenReturn(user);
    when(jsonProcessor.writeValueAsString(any(RentalResponse.class), eq(RentalResponse.class)))
        .thenReturn("{\"serialized\":\"response\"}");

    ResponseEntity<RentalResponse> result = rentalService.rentMovie(IDEMPOTENCY_KEY, request);

    assertEquals(HttpStatus.CREATED, result.getStatusCode());
    assertNotNull(result.getBody());
    assertEquals(USER_ID, result.getBody().userId());
    assertEquals(MOVIE_ID, result.getBody().movieId());
    assertEquals(MOVIE_TITLE, result.getBody().movieTitle());

    verify(rentalRepository, times(1)).save(any(Rental.class));
    verify(movieService, times(1)).updateMovie(movie);
    assertFalse(movie.isAvailable());
    verify(idempotencyKeyService, times(1)).saveIdempotencyKey(eq(IDEMPOTENCY_KEY), anyString());
  }

  @Test
  void rentMovie_usesDefaultRentalPeriod_whenRequestDoesNotSpecifyOne() {
    when(idempotencyKeyService.isKeyUnique(IDEMPOTENCY_KEY)).thenReturn(true);
    when(movieService.isMovieAvailable(MOVIE_ID)).thenReturn(true);
    when(movieService.getMovie(MOVIE_ID)).thenReturn(movie);
    when(userService.getUser(USER_ID)).thenReturn(user);
    when(jsonProcessor.writeValueAsString(any(RentalResponse.class), eq(RentalResponse.class)))
        .thenReturn("{}");

    Instant before = Instant.now();
    ResponseEntity<RentalResponse> result = rentalService.rentMovie(IDEMPOTENCY_KEY, request);
    assertNotNull(result.getBody());
    Instant actualDueDate = result.getBody().dueDate();

    Instant expectedDueDate = before.plus(Duration.ofDays(7));
    long diffSeconds = Math.abs(actualDueDate.getEpochSecond() - expectedDueDate.getEpochSecond());

    assertTrue(
        diffSeconds < 5, "Expected dueDate near " + expectedDueDate + " but was " + actualDueDate);
  }

  @Test
  void rentMovie_usesRequestedRentalPeriod_whenProvidedAndPositive() {
    RentalRequest requestWithCustomPeriod = new RentalRequest(USER_ID, MOVIE_ID, 14);

    when(idempotencyKeyService.isKeyUnique(IDEMPOTENCY_KEY)).thenReturn(true);
    when(movieService.isMovieAvailable(MOVIE_ID)).thenReturn(true);
    when(movieService.getMovie(MOVIE_ID)).thenReturn(movie);
    when(userService.getUser(USER_ID)).thenReturn(user);
    when(jsonProcessor.writeValueAsString(any(RentalResponse.class), eq(RentalResponse.class)))
        .thenReturn("{}");

    Instant before = Instant.now();
    ResponseEntity<RentalResponse> result =
        rentalService.rentMovie(IDEMPOTENCY_KEY, requestWithCustomPeriod);
    Instant expectedDue = before.plusSeconds(14 * 24 * 60 * 60);

    long diffSeconds =
        Math.abs(result.getBody().dueDate().getEpochSecond() - expectedDue.getEpochSecond());
    assertTrue(diffSeconds < 5);
  }

  @Test
  void rentMovie_throwsException_whenMovieIsNotAvailable() {
    when(idempotencyKeyService.isKeyUnique(IDEMPOTENCY_KEY)).thenReturn(true);
    when(movieService.isMovieAvailable(MOVIE_ID)).thenReturn(false);

    assertThrows(
        MovieIsNoLongerAvailableException.class,
        () -> rentalService.rentMovie(IDEMPOTENCY_KEY, request));

    verify(rentalRepository, never()).save(any(Rental.class));
    verify(idempotencyKeyService, never()).saveIdempotencyKey(anyString(), anyString());
  }

  @Test
  void rentMovie_replaysCachedResponse_whenIdempotencyKeyAlreadyUsed() {
    IdempotencyKey existingKey =
        IdempotencyKey.builder().responseBody("{\"cached\":\"response\"}").build();

    RentalResponse cachedResponse =
        new RentalResponse(USER_ID, MOVIE_ID, MOVIE_TITLE, Instant.now(), Instant.now(), null);

    when(idempotencyKeyService.isKeyUnique(IDEMPOTENCY_KEY)).thenReturn(false);
    when(idempotencyKeyService.getIdempotencyKey(IDEMPOTENCY_KEY)).thenReturn(existingKey);
    when(idempotencyKeyService.resolveConflictCause(existingKey, existingKey.getResponseBody()))
        .thenReturn(HttpStatus.OK);
    when(jsonProcessor.readValue(existingKey.getResponseBody(), RentalResponse.class))
        .thenReturn(cachedResponse);

    ResponseEntity<RentalResponse> result = rentalService.rentMovie(IDEMPOTENCY_KEY, request);

    assertEquals(HttpStatus.OK, result.getStatusCode());
    assertEquals(cachedResponse, result.getBody());
    verify(rentalRepository, never()).save(any(Rental.class));
    verify(movieService, never()).getMovie(any());
  }

  @Test
  void rentMovie_returnsConflictStatus_whenIdempotencyKeyReusedWithDifferentBody() {
    IdempotencyKey existingKey =
        IdempotencyKey.builder().responseBody("{\"cached\":\"response\"}").build();

    when(idempotencyKeyService.isKeyUnique(IDEMPOTENCY_KEY)).thenReturn(false);
    when(idempotencyKeyService.getIdempotencyKey(IDEMPOTENCY_KEY)).thenReturn(existingKey);
    when(idempotencyKeyService.resolveConflictCause(existingKey, existingKey.getResponseBody()))
        .thenReturn(HttpStatus.UNPROCESSABLE_ENTITY);
    when(jsonProcessor.readValue(existingKey.getResponseBody(), RentalResponse.class))
        .thenReturn(null);

    ResponseEntity<RentalResponse> result = rentalService.rentMovie(IDEMPOTENCY_KEY, request);

    assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, result.getStatusCode());
    verify(rentalRepository, never()).save(any(Rental.class));
  }
}
