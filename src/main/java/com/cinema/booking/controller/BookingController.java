package com.cinema.booking.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cinema.booking.dto.BookingResponse;
import com.cinema.booking.entity.Booking;
import com.cinema.booking.service.BookingService;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    public record CreateBookingRequest(UUID showtimeId, List<UUID> seatIds) {
    }

    @PostMapping
    public BookingResponse createBooking(@RequestBody CreateBookingRequest request, Authentication authentication) {
        UUID userId = currentUserId(authentication);
        Booking booking = bookingService.createHeldBooking(userId, request.showtimeId(), request.seatIds());
        return BookingResponse.fromEntity(booking);
    }

    @PostMapping("/{id}/confirm")
    public BookingResponse confirmBooking(@PathVariable UUID id) {
        Booking booking = bookingService.confirmBooking(id);
        return BookingResponse.fromEntity(booking);
    }

   @PostMapping("/{id}/cancel")
public BookingResponse cancelBooking(@PathVariable UUID id, Authentication authentication) {
    UUID userId = currentUserId(authentication);
    Booking booking = bookingService.cancelBooking(id, userId);
    return BookingResponse.fromEntity(booking);
}

    @GetMapping("/me")
    public List<BookingResponse> getMyBookings(Authentication authentication) {
        UUID userId = currentUserId(authentication);
        return bookingService.getBookingsByUser(userId).stream()
                .map(BookingResponse::fromEntity)
                .toList();
    }

    private UUID currentUserId(Authentication authentication) {
        String userIdStr = (String) authentication.getPrincipal();
        return UUID.fromString(userIdStr);
    }
}