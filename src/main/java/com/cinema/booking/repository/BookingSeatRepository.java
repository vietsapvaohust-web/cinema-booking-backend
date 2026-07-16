package com.cinema.booking.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cinema.booking.entity.BookingSeat;
import com.cinema.booking.entity.BookingSeatId;

public interface BookingSeatRepository extends JpaRepository<BookingSeat, BookingSeatId> {
    List<BookingSeat> findBySeatId(UUID seatId);
    List<BookingSeat> findByBookingId(UUID bookingId);
}