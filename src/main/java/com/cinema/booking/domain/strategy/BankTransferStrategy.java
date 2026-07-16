package com.cinema.booking.domain.strategy;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class BankTransferStrategy implements PaymentStrategy {

    @Override
    public PaymentResult pay(String bookingId, BigDecimal amount) {
        String fakeTransactionRef = "BANK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return new PaymentResult(true, fakeTransactionRef, "Xác nhận chuyển khoản ngân hàng thành công");
    }

    @Override
    public String getMethodName() {
        return "BANK_TRANSFER";
    }
}