package com.cinema.booking.domain.state;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class BookingStateTest {

    @Test
    void heldBooking_canBeConfirmedToPaid() {
        BookingContext context = new BookingContext(new HeldState());
        context.confirm();
        assertEquals("PAID", context.getCurrentStateName());
    }

    @Test
    void paidBooking_cannotBeConfirmedAgain() {
        BookingContext context = new BookingContext(new HeldState());
        context.confirm();
        assertThrows(IllegalStateException.class, context::confirm);
    }

    @Test
    void heldBooking_canBeCancelledAndReleasesSeats() {
        BookingContext context = new BookingContext(new HeldState());
        context.cancel();
        assertEquals("CANCELLED", context.getCurrentStateName());
        assertTrue(context.isSeatsReleased());
    }

    @Test
    void cancelledBooking_cannotBeConfirmed() {
        BookingContext context = new BookingContext(new HeldState());
        context.cancel();
        assertThrows(IllegalStateException.class, context::confirm);
    }
}