package com.tapeflicks.rentalstore.rental;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {

    List<Rental> findAllRentalsByUserId(Long userId);

    List<Rental> findAllCurrentRentalsByUserId(Long userId);

    List<Rental> findAllReturnedMoviesByUserId(Long userId);
}
