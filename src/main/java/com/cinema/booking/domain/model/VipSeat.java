package com.cinema.booking.domain.model;

import java.math.BigDecimal;

public class VipSeat extends Seat {

    private static final BigDecimal MULTIPLIER = new BigDecimal("1.5");

    public VipSeat(String id, String seatRow, int seatCol) {
        super(id, seatRow, seatCol);
    }

    @Override
    public BigDecimal calculatePrice(BigDecimal basePrice) {
        return basePrice.multiply(MULTIPLIER);
    }

    @Override
    public SeatType getType() {
        return SeatType.VIP;
    }
}