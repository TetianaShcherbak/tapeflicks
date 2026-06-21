package com.tapeflicks.rentalstore.rental;

import com.tapeflicks.rentalstore.movie.Movie;
import com.tapeflicks.rentalstore.user.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "rentals")
@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Rental {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "movie_id", nullable = false)
  private Movie movie;

  @Column(name = "rented_at", nullable = false)
  private LocalDateTime rentedAt;

  @Column(name = "due_date", nullable = false)
  private LocalDateTime dueDate;

  @Column(name = "returned_at")
  private LocalDateTime returnedAt;

  // todo
  @Column(name = "reminder_sent", nullable = false)
  private boolean reminderSent = false;

  public boolean isActive() {
    return returnedAt == null;
  }
}
