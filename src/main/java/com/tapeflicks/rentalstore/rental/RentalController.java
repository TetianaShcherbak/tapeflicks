package com.tapeflicks.rentalstore.rental;

import com.tapeflicks.rentalstore.rental.dto.RentalRequest;
import com.tapeflicks.rentalstore.rental.dto.RentalResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rentals")
public class RentalController {

  private final RentalService rentalService;

  @PostMapping
  public ResponseEntity<RentalResponse> rentMovie(
      @RequestHeader("Idempotency-Key") String idempotencyKey,
      @Valid @RequestBody RentalRequest request) {
    return rentalService.rentMovie(idempotencyKey, request);
  }

  @PutMapping("/{id}/return")
  public ResponseEntity<RentalResponse> returnMovie(
      @PathVariable Long id) {
    RentalResponse rentalResponse = rentalService.returnMovie(id);
    if (rentalResponse == null) {
      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    return ResponseEntity.ok(rentalResponse);
  }

  @GetMapping("/{id}/rentals")
  public List<RentalResponse> getAllRentals(
          @PathVariable Long id) {
      return rentalService.findAllRentedMoviesByUserId(id);
  }

  @GetMapping("/{id}/rentals/active")
  public List<RentalResponse> getActiveRentals(
          @PathVariable Long id) {
      return rentalService.findAllCurrentlyRentedMoviesByUserId(id);
  }

  @GetMapping("/{id}/rentals/returned")
  public List<RentalResponse> getReturnedRentals(
          @PathVariable Long id) {
      return rentalService.findAllReturnedMoviesByUserId(id);
  }
}
