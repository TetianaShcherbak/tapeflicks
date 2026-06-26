package com.tapeflicks.rentalstore.rental;

import com.tapeflicks.rentalstore.rental.dto.RentalRequest;
import com.tapeflicks.rentalstore.rental.dto.RentalResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RentalService {
  @Value("${rentalstore.rental.period-days:7}")
  private int rentalPeriodDaysDefault;

  @Transactional
  public ResponseEntity<RentalResponse> rentMovie(String idempotencyKey, RentalRequest request) {
      return null;
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
