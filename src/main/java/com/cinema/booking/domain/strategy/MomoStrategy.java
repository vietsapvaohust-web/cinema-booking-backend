package com.cinema.booking.domain.strategy;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class MomoStrategy implements PaymentStrategy {

    @Override
    public PaymentResult pay(String bookingId, BigDecimal amount) {
        String fakeTransactionRef = "MOMO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return new PaymentResult(true, fakeTransactionRef, "Thanh toán qua Momo thành công");
    }

    @Override
    public String getMethodName() {
        return "MOMO";
    }
}