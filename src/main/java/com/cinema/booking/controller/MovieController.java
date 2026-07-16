package com.cinema.booking.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cinema.booking.entity.Movie;
import com.cinema.booking.entity.Showtime;
import com.cinema.booking.service.MovieService;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    public List<Movie> getShowingMovies(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String genre) {
        if (search != null || genre != null) {
            return movieService.searchMovies(search, genre);
        }
        return movieService.getShowingMovies();
    }

    @GetMapping("/genres")
    public List<String> getGenres() {
        return movieService.getAllGenres();
    }

    @GetMapping("/all")
    public List<Movie> getAllMovies() {
        return movieService.getAllMovies();
    }

    @GetMapping("/{id}")
    public Movie getMovie(@PathVariable UUID id) {
        return movieService.getById(id);
    }

    @GetMapping("/{id}/showtimes")
    public List<Showtime> getShowtimes(@PathVariable UUID id) {
        return movieService.getShowtimesByMovie(id);
    }

    @PostMapping
    public Movie createMovie(@RequestBody Movie movie) {
        return movieService.createMovie(movie);
    }

    @PutMapping("/{id}")
    public Movie updateMovie(@PathVariable UUID id, @RequestBody Movie movie) {
        return movieService.updateMovie(id, movie);
    }

    @DeleteMapping("/{id}")
    public void deleteMovie(@PathVariable UUID id) {
        movieService.deleteMovie(id);
    }
    @GetMapping("/coming-soon")
public List<Movie> getComingSoonMovies() {
    return movieService.getComingSoonMovies();
}
}