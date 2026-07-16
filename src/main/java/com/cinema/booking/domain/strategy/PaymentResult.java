package com.cinema.booking.domain.strategy;

public record PaymentResult(boolean success, String transactionRef, String message) {
}