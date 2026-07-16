package com.cinema.booking.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.cinema.booking.entity.Booking;
import com.cinema.booking.repository.BookingRepository;
import com.cinema.booking.repository.BookingSeatRepository;

@Component
public class BookingExpirationScheduler {

    private static final Logger log = LoggerFactory.getLogger(BookingExpirationScheduler.class);

    private final BookingRepository bookingRepository;
    private final BookingSeatRepository bookingSeatRepository;
    private final SeatLockService seatLockService;

    public BookingExpirationScheduler(BookingRepository bookingRepository,
                                       BookingSeatRepository bookingSeatRepository,
                                       SeatLockService seatLockService) {
        this.bookingRepository = bookingRepository;
        this.bookingSeatRepository = bookingSeatRepository;
        this.seatLockService = seatLockService;
    }

    // Chạy mỗi 60 giây - quét các booking HELD đã quá hạn giữ chỗ để tự động hủy
    @Scheduled(fixedRate = 60_000)
    @Transactional
    public void expireOverdueBookings() {
        LocalDateTime now = LocalDateTime.now();

        List<Booking> overdueBookings = bookingRepository.findAll().stream()
                .filter(b -> "HELD".equals(b.getStatus()))
                .filter(b -> b.getExpiresAt() != null && b.getExpiresAt().isBefore(now))
                .toList();

        if (overdueBookings.isEmpty()) {
            return;
        }

        for (Booking booking : overdueBookings) {
            booking.setStatus("CANCELLED");
            booking.setUpdatedAt(now);
            bookingRepository.save(booking);

            List<UUID> seatIds = bookingSeatRepository.findAll().stream()
                    .filter(bs -> bs.getBooking().getId().equals(booking.getId()))
                    .map(bs -> bs.getSeat().getId())
                    .toList();

            // Nhả ghế trong Redis (thường TTL cũng đã tự hết hạn cùng lúc,
            // nhưng gọi thêm ở đây để đảm bảo đồng bộ nếu 2 giá trị bị lệch)
            seatLockService.releaseSeats(booking.getShowtime().getId(), seatIds);

            log.info("Đã tự động hủy booking {} do quá hạn giữ chỗ (expiresAt: {})",
                    booking.getId(), booking.getExpiresAt());
        }

        log.info("Scheduler: đã hủy {} booking quá hạn", overdueBookings.size());
    }
}