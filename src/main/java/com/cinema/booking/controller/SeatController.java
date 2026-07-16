package com.cinema.booking.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.cinema.booking.service.SeatService;

@RestController
public class SeatController {

    private final SeatService seatService;

    public SeatController(SeatService seatService) {
        this.seatService = seatService;
    }

    @GetMapping("/api/showtimes/{showtimeId}/seats")
    public List<SeatService.SeatWithStatus> getSeatsForShowtime(@PathVariable UUID showtimeId) {
        return seatService.getSeatsForShowtime(showtimeId);
    }
}