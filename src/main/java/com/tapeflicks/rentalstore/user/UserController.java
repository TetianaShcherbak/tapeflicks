package com.tapeflicks.rentalstore.user;

import com.tapeflicks.rentalstore.rental.RentalService;
import com.tapeflicks.rentalstore.rental.dto.RentalResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
  private final RentalService rentalService;

  @GetMapping("/{id}/rentals")
  public List<RentalResponse> getAllRentals(@PathVariable Long id) {
    return rentalService.findAllRentedMoviesByUserId(id);
  }

  @GetMapping("/{id}/rentals/active")
  public List<RentalResponse> getActiveRentals(@PathVariable Long id) {
    return rentalService.findAllCurrentlyRentedMoviesByUserId(id);
  }

  @GetMapping("/{id}/rentals/returned")
  public List<RentalResponse> getReturnedRentals(@PathVariable Long id) {
    return rentalService.findAllReturnedMoviesByUserId(id);
  }
}
