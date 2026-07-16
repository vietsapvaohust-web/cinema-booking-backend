package com.cinema.booking.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cinema.booking.domain.state.BookingContext;
import com.cinema.booking.domain.state.BookingState;
import com.cinema.booking.domain.state.CancelledState;
import com.cinema.booking.domain.state.HeldState;
import com.cinema.booking.domain.state.PaidState;
import com.cinema.booking.entity.Booking;
import com.cinema.booking.entity.BookingSeat;
import com.cinema.booking.entity.Seat;
import com.cinema.booking.entity.Showtime;
import com.cinema.booking.entity.User;
import com.cinema.booking.repository.BookingRepository;
import com.cinema.booking.repository.BookingSeatRepository;
import com.cinema.booking.repository.SeatRepository;
import com.cinema.booking.repository.ShowtimeRepository;
import com.cinema.booking.repository.UserRepository;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ShowtimeRepository showtimeRepository;
    private final SeatRepository seatRepository;
    private final BookingSeatRepository bookingSeatRepository;
    private final SeatLockService seatLockService;

    public BookingService(BookingRepository bookingRepository,
                           UserRepository userRepository,
                           ShowtimeRepository showtimeRepository,
                           SeatRepository seatRepository,
                           BookingSeatRepository bookingSeatRepository,
                           SeatLockService seatLockService) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.showtimeRepository = showtimeRepository;
        this.seatRepository = seatRepository;
        this.bookingSeatRepository = bookingSeatRepository;
        this.seatLockService = seatLockService;
    }

    @Transactional
    public Booking createHeldBooking(UUID userId, UUID showtimeId, List<UUID> seatIds) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User không tồn tại"));
        Showtime showtime = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new IllegalArgumentException("Showtime không tồn tại"));

        if (seatIds == null || seatIds.isEmpty()) {
            throw new IllegalArgumentException("Phải chọn ít nhất 1 ghế");
        }

        boolean lockAcquired = seatLockService.tryLockSeats(showtimeId, seatIds, userId);
        if (!lockAcquired) {
            throw new IllegalStateException("Một hoặc nhiều ghế đã được người khác giữ, vui lòng chọn ghế khác");
        }

        try {
            List<Seat> seats = seatRepository.findAllById(seatIds);
            if (seats.size() != seatIds.size()) {
                throw new IllegalArgumentException("Một số ghế không tồn tại");
            }

            BigDecimal totalPrice = seats.stream()
                    .map(seat -> calculateSeatPrice(seat, showtime.getBasePrice()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Booking booking = new Booking();
            booking.setUser(user);
            booking.setShowtime(showtime);
            booking.setStatus("HELD");
            booking.setTotalPrice(totalPrice);
            booking.setCreatedAt(LocalDateTime.now());
            booking.setExpiresAt(LocalDateTime.now().plusMinutes(10));
            booking.setUpdatedAt(LocalDateTime.now());

            Booking savedBooking = bookingRepository.save(booking);

            for (Seat seat : seats) {
                BookingSeat bookingSeat = new BookingSeat();
                bookingSeat.setBooking(savedBooking);
                bookingSeat.setSeat(seat);
                bookingSeat.setPriceAtBooking(calculateSeatPrice(seat, showtime.getBasePrice()));
                bookingSeatRepository.save(bookingSeat);
            }

            return savedBooking;

        } catch (Exception e) {
            seatLockService.releaseSeats(showtimeId, seatIds);
            throw e;
        }
    }

    private BigDecimal calculateSeatPrice(Seat seatEntity, BigDecimal basePrice) {
        return com.cinema.booking.domain.factory.SeatFactory.create(
                com.cinema.booking.domain.model.SeatType.valueOf(seatEntity.getSeatType()),
                seatEntity.getId().toString(),
                seatEntity.getSeatRow(),
                seatEntity.getSeatCol()
        ).calculatePrice(basePrice);
    }

    // Tái tạo đúng đối tượng State tương ứng với status hiện tại trong DB,
    // để BookingContext kiểm tra đúng luật chuyển trạng thái (State Pattern)
    private BookingState resolveState(String status) {
        return switch (status) {
            case "HELD" -> new HeldState();
            case "PAID" -> new PaidState();
            case "CANCELLED" -> new CancelledState();
            default -> throw new IllegalStateException("Trạng thái booking không hợp lệ: " + status);
        };
    }

    @Transactional
    public Booking confirmBooking(UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking không tồn tại"));

        BookingContext context = new BookingContext(resolveState(booking.getStatus()));
        context.confirm();

        booking.setStatus(context.getCurrentStateName());
        booking.setUpdatedAt(LocalDateTime.now());

        return bookingRepository.save(booking);
    }

    @Transactional
    public Booking cancelBooking(UUID bookingId, UUID requestingUserId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking không tồn tại"));

        // Chỉ chủ sở hữu booking mới được hủy - tránh trường hợp user A hủy vé của user B
        if (!booking.getUser().getId().equals(requestingUserId)) {
            throw new SecurityException("Bạn không có quyền hủy booking này");
        }

        BookingContext context = new BookingContext(resolveState(booking.getStatus()));
        context.cancel();

        booking.setStatus(context.getCurrentStateName());
        booking.setUpdatedAt(LocalDateTime.now());

        List<UUID> seatIds = bookingSeatRepository.findByBookingId(bookingId).stream()
                .map(bs -> bs.getSeat().getId())
                .toList();
        seatLockService.releaseSeats(booking.getShowtime().getId(), seatIds);

        return bookingRepository.save(booking);
    }

    public List<Booking> getBookingsByUser(UUID userId) {
        return bookingRepository.findByUserId(userId);
    }
}