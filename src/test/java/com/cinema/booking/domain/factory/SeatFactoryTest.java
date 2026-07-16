package com.cinema.booking.domain.factory;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import com.cinema.booking.domain.model.Seat;
import com.cinema.booking.domain.model.SeatType;

class SeatFactoryTest {

    private static final BigDecimal BASE_PRICE = new BigDecimal("50000");

    @Test
    void standardSeat_shouldReturnBasePrice() {
        Seat seat = SeatFactory.create(SeatType.STANDARD, "s1", "A", 1);
        assertEquals(BASE_PRICE, seat.calculatePrice(BASE_PRICE));
    }

    @Test
    void vipSeat_shouldReturnBasePriceTimesOnePointFive() {
        Seat seat = SeatFactory.create(SeatType.VIP, "s2", "A", 2);
        assertEquals(new BigDecimal("75000.0"), seat.calculatePrice(BASE_PRICE));
    }

    @Test
    void coupleSeat_shouldReturnBasePriceTimesTwoPointTwo() {
        Seat seat = SeatFactory.create(SeatType.COUPLE, "s3", "A", 3);
        assertEquals(new BigDecimal("110000.0"), seat.calculatePrice(BASE_PRICE));
    }

    @Test
    void seatLabel_shouldCombineRowAndColumn() {
        Seat seat = SeatFactory.create(SeatType.STANDARD, "s4", "B", 5);
        assertEquals("B5", seat.getLabel());
    }
}