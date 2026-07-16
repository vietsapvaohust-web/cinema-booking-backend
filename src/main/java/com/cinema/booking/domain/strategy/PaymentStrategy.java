package com.cinema.booking.domain.strategy;

import java.math.BigDecimal;

public interface PaymentStrategy {

    PaymentResult pay(String bookingId, BigDecimal amount);

    String getMethodName();
}