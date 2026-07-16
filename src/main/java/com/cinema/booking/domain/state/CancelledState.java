package com.cinema.booking.domain.state;

public class CancelledState implements BookingState {

    @Override
    public void confirm(BookingContext context) {
        throw new IllegalStateException("Booking đã bị hủy, không thể confirm.");
    }

    @Override
    public void cancel(BookingContext context) {
        // Đã hủy rồi, không cần làm gì thêm
    }

    @Override
    public String getName() {
        return "CANCELLED";
    }
}