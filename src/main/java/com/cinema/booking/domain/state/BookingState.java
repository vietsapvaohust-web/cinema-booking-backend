package com.cinema.booking.domain.state;

public interface BookingState {

    void confirm(BookingContext context);

    void cancel(BookingContext context);

    String getName();
}