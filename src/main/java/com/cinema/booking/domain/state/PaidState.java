package com.cinema.booking.domain.state;

public class PaidState implements BookingState {

    @Override
    public void confirm(BookingContext context) {
        throw new IllegalStateException("Booking đã được thanh toán, không thể confirm lại.");
    }

    @Override
    public void cancel(BookingContext context) {
        context.setState(new CancelledState());
        context.releaseSeats();
        // Ở đây có thể trigger thêm logic hoàn tiền (refund)
    }

    @Override
    public String getName() {
        return "PAID";
    }
}