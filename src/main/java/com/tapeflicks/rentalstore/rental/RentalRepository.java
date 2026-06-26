package com.tapeflicks.rentalstore.rental;

import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {

  /**
   * Finds active rentals whose due date falls at or before the given threshold and for which a
   * reminder has not yet been sent.
   *
   * <p>Used by the due-date reminder scheduler to identify rentals that require a notification
   * within the upcoming reminder window.
   *
   * @param threshold the cutoff timestamp; rentals due at or before this instant are returned
   * @return list of active unreturned rentals due before the threshold, or empty list if none
   */
  @Query(
      """
            SELECT r FROM Rental r
            WHERE r.returnedAt IS NULL
              AND r.reminderSent = false
              AND r.dueDate <= :threshold
            """)
  List<Rental> findActiveRentalsDueBefore(@Param("threshold") Instant threshold);

  /**
   * Retrieves the full rental history for a user, including both active and returned rentals.
   *
   * @param userId ID of the user whose rentals to fetch
   * @return list of all rentals belonging to the user, or empty list if none exist
   */
  @Query(
      """
            SELECT r FROM Rental r
            WHERE r.user.id = :userId
            """)
  List<Rental> findAllRentalsByUserId(@Param("userId") Long userId);

  /**
   * Retrieves all active (not yet returned) rentals for a user.
   *
   * @param userId ID of the user whose active rentals to fetch
   * @return list of rentals where {@code returnedAt} is null, or empty list if none exist
   */
  @Query(
      """
            SELECT r FROM Rental r
            WHERE r.user.id = :userId
              AND r.returnedAt IS NULL
            """)
  List<Rental> findAllCurrentRentalsByUserId(
      @Param("userId") Long userId); // fix: added missing @Param

  /**
   * Retrieves all completed (returned) rentals for a user.
   *
   * @param userId ID of the user whose returned rentals to fetch
   * @return list of rentals where {@code returnedAt} is set, or empty list if none exist
   */
  @Query(
      """
            SELECT r FROM Rental r
            WHERE r.user.id = :userId
              AND r.returnedAt IS NOT NULL
            """)
  List<Rental> findAllReturnedMoviesByUserId(
      @Param("userId") Long userId); // fix: added missing @Param
}
