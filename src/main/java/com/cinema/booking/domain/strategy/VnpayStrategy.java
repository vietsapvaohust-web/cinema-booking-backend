package com.cinema.booking.domain.strategy;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class VnpayStrategy implements PaymentStrategy {

    @Override
    public PaymentResult pay(String bookingId, BigDecimal amount) {
        // Đây là phần mô phỏng (mock) - thực tế sẽ gọi API sandbox của VNPay,
        // redirect người dùng sang trang thanh toán, rồi nhận callback xác nhận.
        // Cho mục đích đồ án, ta giả lập luôn giao dịch thành công.
        String fakeTransactionRef = "VNPAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return new PaymentResult(true, fakeTransactionRef, "Thanh toán qua VNPay thành công");
    }

    @Override
    public String getMethodName() {
        return "VNPAY";
    }
}