package com.cinema.booking.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cinema.booking.domain.strategy.PaymentResult;
import com.cinema.booking.domain.strategy.PaymentStrategy;
import com.cinema.booking.domain.strategy.PaymentStrategyFactory;
import com.cinema.booking.entity.Booking;
import com.cinema.booking.entity.Payment;
import com.cinema.booking.repository.BookingRepository;
import com.cinema.booking.repository.PaymentRepository;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final PaymentStrategyFactory paymentStrategyFactory;

    public PaymentService(PaymentRepository paymentRepository,
                           BookingRepository bookingRepository,
                           PaymentStrategyFactory paymentStrategyFactory) {
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
        this.paymentStrategyFactory = paymentStrategyFactory;
    }

    @Transactional
    public Payment processPayment(UUID bookingId, String method) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking không tồn tại"));

        if (!"HELD".equals(booking.getStatus())) {
            throw new IllegalStateException("Chỉ có thể thanh toán booking đang ở trạng thái HELD");
        }

        // Đây chính là điểm hay của Strategy Pattern: chỉ cần đổi tên method,
        // toàn bộ logic xử lý thanh toán sẽ tự động chọn đúng lớp tương ứng
        // mà không cần sửa if-else ở đây
        PaymentStrategy strategy = paymentStrategyFactory.getStrategy(method);
        PaymentResult result = strategy.pay(bookingId.toString(), booking.getTotalPrice());

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setMethod(strategy.getMethodName());
        payment.setAmount(booking.getTotalPrice());
        payment.setStatus(result.success() ? "SUCCESS" : "FAILED");
        payment.setTransactionRef(result.transactionRef());
        payment.setPaidAt(result.success() ? LocalDateTime.now() : null);
        payment.setCreatedAt(LocalDateTime.now());

        Payment savedPayment = paymentRepository.save(payment);

        if (result.success()) {
            booking.setStatus("PAID");
            booking.setUpdatedAt(LocalDateTime.now());
            bookingRepository.save(booking);
        }

        return savedPayment;
    }
}