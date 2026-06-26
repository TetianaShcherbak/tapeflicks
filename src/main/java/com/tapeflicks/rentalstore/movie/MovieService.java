package com.tapeflicks.rentalstore.movie;

import com.tapeflicks.rentalstore.movie.dto.MovieRequest;
import com.tapeflicks.rentalstore.movie.dto.MovieResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieService {
    // PATCH
    @Transactional
    public MovieResponse updateMovie(MovieRequest request) {
        return null;
    }

    // PUT
    @Transactional
    public MovieResponse updateMovie(Movie movie) {
        return null;
    }

    public boolean isMovieAvailable(Long movieId) {
        return false;
    }

    public Movie getMovie(Long id) {
        return null;
    }
}
