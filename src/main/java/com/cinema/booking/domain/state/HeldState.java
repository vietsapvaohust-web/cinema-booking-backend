package com.cinema.booking.domain.state;

public class HeldState implements BookingState {

    @Override
    public void confirm(BookingContext context) {
        context.setState(new PaidState());
    }

    @Override
    public void cancel(BookingContext context) {
        context.setState(new CancelledState());
        context.releaseSeats();
    }

    @Override
    public String getName() {
        return "HELD";
    }
}