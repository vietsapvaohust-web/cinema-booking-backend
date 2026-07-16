package com.cinema.booking.domain.factory;

import com.cinema.booking.domain.model.CoupleSeat;
import com.cinema.booking.domain.model.Seat;
import com.cinema.booking.domain.model.SeatType;
import com.cinema.booking.domain.model.StandardSeat;
import com.cinema.booking.domain.model.VipSeat;

public class SeatFactory {

    // Private constructor - đây là utility class, không cần khởi tạo instance
    private SeatFactory() {
    }

    public static Seat create(SeatType type, String id, String seatRow, int seatCol) {
        return switch (type) {
            case STANDARD -> new StandardSeat(id, seatRow, seatCol);
            case VIP -> new VipSeat(id, seatRow, seatCol);
            case COUPLE -> new CoupleSeat(id, seatRow, seatCol);
        };
    }
}