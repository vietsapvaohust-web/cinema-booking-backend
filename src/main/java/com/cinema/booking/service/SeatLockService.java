package com.cinema.booking.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Service
public class SeatLockService {

    private static final String LOCK_PREFIX = "seat_lock:";
    private static final Duration LOCK_DURATION = Duration.ofMinutes(10); // khớp với thời gian giữ ghế của Booking

    private final StringRedisTemplate redisTemplate;

    public SeatLockService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Cố gắng khóa 1 ghế cho 1 showtime cụ thể.
     * Dùng lệnh SET key value NX EX - chỉ set được nếu key CHƯA tồn tại (NX),
     * và tự hết hạn sau LOCK_DURATION (EX) - đây chính là cơ chế "giữ ghế tạm thời".
     *
     * @return true nếu khóa thành công (ghế đang trống), false nếu ghế đã bị người khác giữ/đặt
     */
    public boolean tryLockSeat(UUID showtimeId, UUID seatId, UUID userId) {
        String key = buildKey(showtimeId, seatId);
        Boolean success = redisTemplate.opsForValue()
                .setIfAbsent(key, userId.toString(), LOCK_DURATION);
        return Boolean.TRUE.equals(success);
    }

    /**
     * Khóa nhiều ghế cùng lúc theo kiểu "tất cả hoặc không gì cả" (all-or-nothing).
     * Nếu 1 ghế bất kỳ trong danh sách đã bị khóa trước đó, toàn bộ các ghế
     * vừa khóa thành công trước đó trong vòng lặp sẽ được nhả ra ngay lập tức (rollback thủ công),
     * tránh tình trạng khóa được nửa chừng rồi treo ghế của người khác.
     */
    public boolean tryLockSeats(UUID showtimeId, List<UUID> seatIds, UUID userId) {
        List<UUID> lockedSoFar = new java.util.ArrayList<>();

        for (UUID seatId : seatIds) {
            boolean locked = tryLockSeat(showtimeId, seatId, userId);
            if (!locked) {
                // Rollback: nhả lại toàn bộ ghế đã khóa được trước đó trong lần gọi này
                releaseSeats(showtimeId, lockedSoFar);
                return false;
            }
            lockedSoFar.add(seatId);
        }
        return true;
    }

    public void releaseSeat(UUID showtimeId, UUID seatId) {
        redisTemplate.delete(buildKey(showtimeId, seatId));
    }

    public void releaseSeats(UUID showtimeId, List<UUID> seatIds) {
        seatIds.forEach(seatId -> releaseSeat(showtimeId, seatId));
    }

    public boolean isSeatLocked(UUID showtimeId, UUID seatId) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(buildKey(showtimeId, seatId)));
    }

    private String buildKey(UUID showtimeId, UUID seatId) {
        return LOCK_PREFIX + showtimeId + ":" + seatId;
    }
}