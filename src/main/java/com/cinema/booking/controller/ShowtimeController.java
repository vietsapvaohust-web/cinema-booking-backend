package com.cinema.booking.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cinema.booking.entity.Movie;
import com.cinema.booking.entity.Room;
import com.cinema.booking.entity.Showtime;
import com.cinema.booking.repository.MovieRepository;
import com.cinema.booking.repository.RoomRepository;
import com.cinema.booking.repository.ShowtimeRepository;

@RestController
@RequestMapping("/api/showtimes")
public class ShowtimeController {

    private final ShowtimeRepository showtimeRepository;
    private final MovieRepository movieRepository;
    private final RoomRepository roomRepository;

    public ShowtimeController(ShowtimeRepository showtimeRepository,
                               MovieRepository movieRepository,
                               RoomRepository roomRepository) {
        this.showtimeRepository = showtimeRepository;
        this.movieRepository = movieRepository;
        this.roomRepository = roomRepository;
    }

    public record ShowtimeDetail(UUID id, UUID roomId, UUID movieId, String movieTitle,
                                   LocalDateTime startTime, BigDecimal basePrice) {
    }

    public record CreateShowtimeRequest(UUID movieId, UUID roomId, LocalDateTime startTime,
                                          Integer durationMinutes, BigDecimal basePrice) {
    }

    @GetMapping("/{id}")
    public ShowtimeDetail getShowtime(@PathVariable UUID id) {
        Showtime showtime = showtimeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Showtime không tồn tại"));

        return new ShowtimeDetail(
                showtime.getId(),
                showtime.getRoom().getId(),
                showtime.getMovie().getId(),
                showtime.getMovie().getTitle(),
                showtime.getStartTime(),
                showtime.getBasePrice()
        );
    }

    @PostMapping
    public Showtime createShowtime(@RequestBody CreateShowtimeRequest request) {
        Movie movie = movieRepository.findById(request.movieId())
                .orElseThrow(() -> new IllegalArgumentException("Phim không tồn tại"));
        Room room = roomRepository.findById(request.roomId())
                .orElseThrow(() -> new IllegalArgumentException("Phòng không tồn tại"));

        Showtime showtime = new Showtime();
        showtime.setMovie(movie);
        showtime.setRoom(room);
        showtime.setStartTime(request.startTime());
        showtime.setEndTime(request.startTime().plusMinutes(request.durationMinutes()));
        showtime.setBasePrice(request.basePrice());

        return showtimeRepository.save(showtime);
    }

    @DeleteMapping("/{id}")
    public void deleteShowtime(@PathVariable UUID id) {
        showtimeRepository.deleteById(id);
    }
}