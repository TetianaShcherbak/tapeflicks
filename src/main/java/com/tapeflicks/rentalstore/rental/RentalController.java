package com.tapeflicks.rentalstore.rental;

import com.tapeflicks.rentalstore.rental.dto.RentalRequest;
import com.tapeflicks.rentalstore.rental.dto.RentalResponse;
import com.tapeflicks.rentalstore.security.AppUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rentals")
public class RentalController {

  private final RentalService rentalService;

  @PostMapping
  public ResponseEntity<RentalResponse> rentMovie(
      @RequestHeader("Idempotency-Key") String idempotencyKey,
      @Valid @RequestBody RentalRequest request,
      @AuthenticationPrincipal AppUserDetails currentUser) {
    rentalService.verifyOwnership(request.userId(), currentUser.getId());
    return rentalService.rentMovie(idempotencyKey, request);
  }

  @PutMapping("/{id}/return")
  public ResponseEntity<RentalResponse> returnMovie(
      @PathVariable Long id, @AuthenticationPrincipal AppUserDetails currentUser) {
    rentalService.verifyOwnership(id, currentUser.getId());
    RentalResponse rentalResponse = rentalService.returnMovie(id);
    if (rentalResponse == null) {
      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    return ResponseEntity.ok(rentalResponse);
  }
}
