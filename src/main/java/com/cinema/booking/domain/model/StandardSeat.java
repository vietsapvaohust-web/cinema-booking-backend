package com.cinema.booking.domain.model;

import java.math.BigDecimal;

public class StandardSeat extends Seat {

    public StandardSeat(String id, String seatRow, int seatCol) {
        super(id, seatRow, seatCol);
    }

    @Override
    public BigDecimal calculatePrice(BigDecimal basePrice) {
        return basePrice;
    }

    @Override
    public SeatType getType() {
        return SeatType.STANDARD;
    }
}