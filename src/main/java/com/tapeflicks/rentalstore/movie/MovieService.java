package com.tapeflicks.rentalstore.movie;

import com.tapeflicks.rentalstore.movie.dto.MovieRequest;
import com.tapeflicks.rentalstore.movie.dto.MovieResponse;
import com.tapeflicks.rentalstore.movie.exception.MovieNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieService {
    private final MovieRepository movieRepository;

    // PATCH
    @Transactional
    public MovieResponse updateMovie(MovieRequest request) {
        Movie movie =
                movieRepository
                        .findById(request.id())
                        .orElseThrow(() -> new MovieNotFoundException(request.id()));
        if (!request.title().isEmpty())
            movie.setTitle(request.title());
        if (!request.genre().isEmpty()) movie.setGenre(request.genre());
        movie.setAvailable(request.available());
        movieRepository.save(movie);

        return MovieResponse.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .genre(movie.getGenre())
                .available(movie.isAvailable())
                .build();
    }

    // PUT
    @Transactional
    public MovieResponse updateMovie(Movie movie) {
        Movie movieRecord =
                movieRepository
                        .findById(movie.getId())
                        .orElseThrow(() -> new MovieNotFoundException(movie.getId()));
        movieRepository.save(movieRecord);

        return MovieResponse.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .genre(movie.getGenre())
                .available(movie.isAvailable())
                .build();
    }

    public boolean isMovieAvailable(Long movieId) {
        Movie movie =
                movieRepository.findById(movieId).orElseThrow(() -> new MovieNotFoundException(movieId));

        return movie.isAvailable();
    }

    public Movie getMovie(Long id) {
        return movieRepository.findById(id).orElseThrow(() -> new MovieNotFoundException(id));
    }

    public MovieResponse getMovieResponse(Long id) {
        Movie movie = getMovie(id);
        return MovieResponse.builder()
                .id(id)
                .title(movie.getTitle())
                .genre(movie.getGenre())
                .available(movie.isAvailable())
                .build();
    }
}
