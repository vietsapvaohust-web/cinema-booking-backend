package com.cinema.booking.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.cinema.booking.entity.Movie;
import com.cinema.booking.entity.Showtime;
import com.cinema.booking.repository.MovieRepository;
import com.cinema.booking.repository.ShowtimeRepository;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final ShowtimeRepository showtimeRepository;

    public MovieService(MovieRepository movieRepository, ShowtimeRepository showtimeRepository) {
        this.movieRepository = movieRepository;
        this.showtimeRepository = showtimeRepository;
    }

    public List<Movie> getShowingMovies() {
        return movieRepository.findByStatus("SHOWING");
    }

    public List<Movie> getComingSoonMovies() {
        return movieRepository.findByStatus("COMING_SOON");
    }

    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    public Movie getById(UUID id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Movie không tồn tại"));
    }

    public List<Showtime> getShowtimesByMovie(UUID movieId) {
        return showtimeRepository.findByMovieId(movieId);
    }

    public Movie createMovie(Movie movie) {
        movie.setStatus(movie.getStatus() == null ? "SHOWING" : movie.getStatus());
        return movieRepository.save(movie);
    }

    public Movie updateMovie(UUID id, Movie updated) {
        Movie existing = getById(id);
        existing.setTitle(updated.getTitle());
        existing.setDescription(updated.getDescription());
        existing.setDurationMinutes(updated.getDurationMinutes());
        existing.setPosterUrl(updated.getPosterUrl());
        existing.setGenre(updated.getGenre());
        existing.setAgeRating(updated.getAgeRating());
        existing.setStatus(updated.getStatus());
        return movieRepository.save(existing);
    }

    public void deleteMovie(UUID id) {
        Movie movie = getById(id);
        movieRepository.delete(movie);
    }

    public List<Movie> searchMovies(String search, String genre) {
        return movieRepository.searchShowingMovies(
                (search == null || search.isBlank()) ? null : search,
                (genre == null || genre.isBlank()) ? null : genre
        );
    }

    public List<String> getAllGenres() {
        return movieRepository.findDistinctGenres();
    }
}