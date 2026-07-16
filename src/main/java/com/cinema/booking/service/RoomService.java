package com.cinema.booking.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cinema.booking.entity.Room;
import com.cinema.booking.entity.Seat;
import com.cinema.booking.repository.RoomRepository;
import com.cinema.booking.repository.SeatRepository;
import com.cinema.booking.repository.ShowtimeRepository;

@Service
public class RoomService {

    // Cấu hình sơ đồ ghế mặc định - áp dụng giống nhau cho MỌI phòng chiếu
    private static final int ROW_COUNT = 8;
    private static final int COL_COUNT = 10;

    private final RoomRepository roomRepository;
    private final SeatRepository seatRepository;
    private final ShowtimeRepository showtimeRepository;

    public RoomService(RoomRepository roomRepository,
                        SeatRepository seatRepository,
                        ShowtimeRepository showtimeRepository) {
        this.roomRepository = roomRepository;
        this.seatRepository = seatRepository;
        this.showtimeRepository = showtimeRepository;
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    // Tạo phòng mới VÀ tự động sinh sơ đồ ghế chuẩn (giống nhau cho mọi phòng)
    @Transactional
    public Room createRoom(String name, String roomType) {
        Room room = new Room();
        room.setName(name);
        room.setRoomType(roomType == null ? "STANDARD" : roomType);
        room.setRowCount(ROW_COUNT);
        room.setColCount(COL_COUNT);
        room.setIsActive(true);

        Room savedRoom = roomRepository.save(room);

        generateDefaultSeats(savedRoom);

        return savedRoom;
    }

    private void generateDefaultSeats(Room room) {
        for (int row = 1; row <= ROW_COUNT; row++) {
            String rowLabel = String.valueOf((char) ('A' + row - 1));
            String seatType = resolveDefaultSeatType(row);

            for (int col = 1; col <= COL_COUNT; col++) {
                Seat seat = new Seat();
                seat.setRoom(room);
                seat.setSeatRow(rowLabel);
                seat.setSeatCol(col);
                seat.setSeatType(seatType);
                seatRepository.save(seat);
            }
        }
    }

    // Quy tắc phân loại ghế mặc định theo hàng - áp dụng đồng nhất cho mọi phòng:
    // 2 hàng đầu: STANDARD, 4 hàng giữa: VIP, 2 hàng cuối: COUPLE
    private String resolveDefaultSeatType(int rowNumber) {
        if (rowNumber <= 2) return "STANDARD";
        if (rowNumber <= 6) return "VIP";
        return "COUPLE";
    }

    @Transactional
    public void deleteRoom(UUID roomId) {
        boolean hasShowtimes = !showtimeRepository.findAll().stream()
                .filter(s -> s.getRoom().getId().equals(roomId))
                .toList().isEmpty();

        if (hasShowtimes) {
            throw new IllegalStateException("Không thể xóa phòng đang có suất chiếu. Vui lòng xóa suất chiếu trước.");
        }

        roomRepository.deleteById(roomId);
    }
}