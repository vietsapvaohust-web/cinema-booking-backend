package com.cinema.booking.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cinema.booking.entity.Booking;
import com.cinema.booking.entity.Seat;
import com.cinema.booking.entity.Showtime;
import com.cinema.booking.entity.User;
import com.cinema.booking.repository.BookingRepository;
import com.cinema.booking.repository.BookingSeatRepository;
import com.cinema.booking.repository.SeatRepository;
import com.cinema.booking.repository.ShowtimeRepository;
import com.cinema.booking.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock private BookingRepository bookingRepository;
    @Mock private UserRepository userRepository;
    @Mock private ShowtimeRepository showtimeRepository;
    @Mock private SeatRepository seatRepository;
    @Mock private BookingSeatRepository bookingSeatRepository;
    @Mock private SeatLockService seatLockService;

    @InjectMocks
    private BookingService bookingService;

    private UUID userId;
    private UUID showtimeId;
    private UUID seatId;
    private User user;
    private Showtime showtime;
    private Seat seat;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        showtimeId = UUID.randomUUID();
        seatId = UUID.randomUUID();

        user = new User();
        user.setId(userId);
        user.setEmail("test@example.com");

        showtime = new Showtime();
        showtime.setId(showtimeId);
        showtime.setBasePrice(new BigDecimal("50000"));

        seat = new Seat();
        seat.setId(seatId);
        seat.setSeatRow("A");
        seat.setSeatCol(1);
        seat.setSeatType("STANDARD");
    }

    @Test
    void createHeldBooking_shouldFail_whenSeatLockNotAcquired() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(showtimeRepository.findById(showtimeId)).thenReturn(Optional.of(showtime));
        // Giả lập tình huống: ghế đã bị người khác giữ trước đó -> lock thất bại
        when(seatLockService.tryLockSeats(eq(showtimeId), anyList(), eq(userId))).thenReturn(false);

        assertThrows(IllegalStateException.class, () ->
                bookingService.createHeldBooking(userId, showtimeId, List.of(seatId))
        );

        // Đảm bảo KHÔNG có booking nào được lưu khi lock thất bại
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createHeldBooking_shouldSucceed_whenSeatLockAcquired() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(showtimeRepository.findById(showtimeId)).thenReturn(Optional.of(showtime));
        when(seatLockService.tryLockSeats(eq(showtimeId), anyList(), eq(userId))).thenReturn(true);
        when(seatRepository.findAllById(anyList())).thenReturn(List.of(seat));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));

        Booking result = bookingService.createHeldBooking(userId, showtimeId, List.of(seatId));

        assertEquals("HELD", result.getStatus());
        assertEquals(new BigDecimal("50000"), result.getTotalPrice());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void createHeldBooking_shouldReleaseSeats_whenErrorOccursAfterLockAcquired() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(showtimeRepository.findById(showtimeId)).thenReturn(Optional.of(showtime));
        when(seatLockService.tryLockSeats(eq(showtimeId), anyList(), eq(userId))).thenReturn(true);
        // Giả lập ghế không tìm thấy trong DB (dữ liệu không nhất quán) -> gây lỗi sau khi đã lock
        when(seatRepository.findAllById(anyList())).thenReturn(List.of());

        assertThrows(IllegalArgumentException.class, () ->
                bookingService.createHeldBooking(userId, showtimeId, List.of(seatId))
        );

        // Xác nhận ghế đã được nhả lại đúng 1 lần khi có lỗi xảy ra sau khi lock thành công
        verify(seatLockService, times(1)).releaseSeats(eq(showtimeId), anyList());
    }

    @Test
    void createHeldBooking_shouldThrow_whenNoSeatsProvided() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(showtimeRepository.findById(showtimeId)).thenReturn(Optional.of(showtime));

        assertThrows(IllegalArgumentException.class, () ->
                bookingService.createHeldBooking(userId, showtimeId, List.of())
        );

        verify(seatLockService, never()).tryLockSeats(any(), any(), any());
    }

    @Test
    void createHeldBooking_shouldThrow_whenUserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                bookingService.createHeldBooking(userId, showtimeId, List.of(seatId))
        );
    }
}