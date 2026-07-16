package com.cinema.booking.domain.model;

import java.math.BigDecimal;

public abstract class Seat {

    protected String id;
    protected String seatRow;
    protected int seatCol;

    public Seat(String id, String seatRow, int seatCol) {
        this.id = id;
        this.seatRow = seatRow;
        this.seatCol = seatCol;
    }

    public abstract BigDecimal calculatePrice(BigDecimal basePrice);

    public abstract SeatType getType();

    public String getId() {
        return id;
    }

    public String getSeatRow() {
        return seatRow;
    }

    public int getSeatCol() {
        return seatCol;
    }

    public String getLabel() {
        return seatRow + seatCol;
    }
}