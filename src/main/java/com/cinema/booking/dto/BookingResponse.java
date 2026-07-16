package com.cinema.booking.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.cinema.booking.entity.Booking;

public record BookingResponse(
        UUID id,
        String status,
        BigDecimal totalPrice,
        LocalDateTime createdAt,
        LocalDateTime expiresAt,
        String userEmail,
        String userFullName,
        UUID showtimeId,
        LocalDateTime showtimeStartTime
) {
    public static BookingResponse fromEntity(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getStatus(),
                booking.getTotalPrice(),
                booking.getCreatedAt(),
                booking.getExpiresAt(),
                booking.getUser().getEmail(),
                booking.getUser().getFullName(),
                booking.getShowtime().getId(),
                booking.getShowtime().getStartTime()
        );
    }
}