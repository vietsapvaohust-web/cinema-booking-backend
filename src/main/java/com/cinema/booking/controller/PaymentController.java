package com.cinema.booking.controller;

import java.util.UUID;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cinema.booking.entity.Payment;
import com.cinema.booking.service.PaymentService;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public record PaymentRequest(UUID bookingId, String method) {
    }

    @PostMapping
    public Payment pay(@RequestBody PaymentRequest request) {
        return paymentService.processPayment(request.bookingId(), request.method());
    }
}