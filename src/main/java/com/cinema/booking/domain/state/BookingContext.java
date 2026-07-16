package com.cinema.booking.domain.state;

public class BookingContext {

    private BookingState currentState;
    private boolean seatsReleased = false;

    public BookingContext(BookingState initialState) {
        this.currentState = initialState;
    }

    public void confirm() {
        currentState.confirm(this);
    }

    public void cancel() {
        currentState.cancel(this);
    }

    public void setState(BookingState state) {
        this.currentState = state;
    }

    public String getCurrentStateName() {
        return currentState.getName();
    }

    public void releaseSeats() {
        this.seatsReleased = true;
    }

    public boolean isSeatsReleased() {
        return seatsReleased;
    }
}