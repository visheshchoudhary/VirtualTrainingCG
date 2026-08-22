package com.founderlink.payment.controller;

import com.founderlink.payment.dto.PaymentRequest;
import com.founderlink.payment.dto.PaymentResponse;
import com.founderlink.payment.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/payments")
public class PaymentController {

    // Logger
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    private PaymentService paymentService;

    // Make payment
    @PostMapping
    public ResponseEntity<PaymentResponse> makePayment(
            @RequestBody PaymentRequest request) {
        logger.info("POST /payments - makePayment called");
        return ResponseEntity.ok(paymentService.makePayment(request));
    }

    // Get payment by ID
    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPaymentById(
            @PathVariable Long id) {
        logger.info("GET /payments/{} - getPaymentById called", id);
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }

    // Get all payments
    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getAllPayments() {
        logger.info("GET /payments - getAllPayments called");
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    // Get payments by sender
    @GetMapping("/sender/{senderId}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsBySender(
            @PathVariable Long senderId) {
        logger.info("GET /payments/sender/{} called", senderId);
        return ResponseEntity.ok(paymentService.getPaymentsBySender(senderId));
    }

    // Get payments by receiver
    @GetMapping("/receiver/{receiverId}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByReceiver(
            @PathVariable Long receiverId) {
        logger.info("GET /payments/receiver/{} called", receiverId);
        return ResponseEntity.ok(paymentService.getPaymentsByReceiver(receiverId));
    }

    // Get payments by investment
    @GetMapping("/investment/{investmentId}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByInvestment(
            @PathVariable Long investmentId) {
        logger.info("GET /payments/investment/{} called", investmentId);
        return ResponseEntity.ok(paymentService.getPaymentsByInvestment(investmentId));
    }

    // Mark payment as SUCCESS
    @PutMapping("/{id}/success")
    public ResponseEntity<PaymentResponse> markSuccess(
            @PathVariable Long id) {
        logger.info("PUT /payments/{}/success called", id);
        return ResponseEntity.ok(paymentService.markSuccess(id));
    }

    // Mark payment as FAILED
    @PutMapping("/{id}/failed")
    public ResponseEntity<PaymentResponse> markFailed(
            @PathVariable Long id) {
        logger.info("PUT /payments/{}/failed called", id);
        return ResponseEntity.ok(paymentService.markFailed(id));
    }
}