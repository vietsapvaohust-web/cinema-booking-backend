package com.cinema.booking.service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.cinema.booking.domain.factory.SeatFactory;
import com.cinema.booking.domain.model.Seat;
import com.cinema.booking.domain.model.SeatType;
import com.cinema.booking.entity.Showtime;
import com.cinema.booking.repository.BookingRepository;
import com.cinema.booking.repository.BookingSeatRepository;
import com.cinema.booking.repository.SeatRepository;
import com.cinema.booking.repository.ShowtimeRepository;

@Service
public class SeatService {

    private final SeatRepository seatRepository;
    private final ShowtimeRepository showtimeRepository;
    private final BookingRepository bookingRepository;
    private final BookingSeatRepository bookingSeatRepository;

    public SeatService(SeatRepository seatRepository,
                       ShowtimeRepository showtimeRepository,
                       BookingRepository bookingRepository,
                       BookingSeatRepository bookingSeatRepository) {
        this.seatRepository = seatRepository;
        this.showtimeRepository = showtimeRepository;
        this.bookingRepository = bookingRepository;
        this.bookingSeatRepository = bookingSeatRepository;
    }

    public record SeatWithStatus(String id, String label, String type, java.math.BigDecimal price, boolean taken) {
    }

    public List<Seat> getSeatsByRoom(UUID roomId) {
        return seatRepository.findByRoomId(roomId).stream()
                .map(entity -> SeatFactory.create(
                        SeatType.valueOf(entity.getSeatType()),
                        entity.getId().toString(),
                        entity.getSeatRow(),
                        entity.getSeatCol()
                ))
                .toList();
    }

    // Trả về ghế của phòng kèm trạng thái "đã bị đặt" cho ĐÚNG suất chiếu này
    public List<SeatWithStatus> getSeatsForShowtime(UUID showtimeId) {
        Showtime showtime = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new IllegalArgumentException("Showtime không tồn tại"));

        // Lấy toàn bộ booking KHÔNG bị hủy (HELD hoặc PAID) của đúng suất chiếu này
        Set<UUID> takenSeatIds = bookingRepository.findAll().stream()
                .filter(b -> b.getShowtime().getId().equals(showtimeId))
                .filter(b -> !"CANCELLED".equals(b.getStatus()))
                .flatMap(b -> bookingSeatRepository.findAll().stream()
                        .filter(bs -> bs.getBooking().getId().equals(b.getId())))
                .map(bs -> bs.getSeat().getId())
                .collect(Collectors.toSet());

        return seatRepository.findByRoomId(showtime.getRoom().getId()).stream()
                .map(entity -> {
                    Seat domainSeat = SeatFactory.create(
                            SeatType.valueOf(entity.getSeatType()),
                            entity.getId().toString(),
                            entity.getSeatRow(),
                            entity.getSeatCol()
                    );
                    return new SeatWithStatus(
                            entity.getId().toString(),
                            domainSeat.getLabel(),
                            domainSeat.getType().name(),
                            domainSeat.calculatePrice(showtime.getBasePrice()),
                            takenSeatIds.contains(entity.getId())
                    );
                })
                .toList();
    }
}