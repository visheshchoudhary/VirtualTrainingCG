package com.founderlink.payment.service;

import com.founderlink.payment.dto.PaymentRequest;
import com.founderlink.payment.dto.PaymentResponse;
import com.founderlink.payment.entity.Payment;
import com.founderlink.payment.event.EventPublisher;
import com.founderlink.payment.exception.InvalidInputException;
import com.founderlink.payment.exception.ResourceNotFoundException;
import com.founderlink.payment.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    // Logger
    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private EventPublisher eventPublisher;

    // Make payment
    public PaymentResponse makePayment(PaymentRequest request) {

        logger.info("Processing payment for investmentId: {}", request.getInvestmentId());

        // Validate investmentId
        if (request.getInvestmentId() == null) {
            throw new InvalidInputException("Investment ID cannot be null!");
        }

        // Validate senderId
        if (request.getSenderId() == null) {
            throw new InvalidInputException("Sender ID cannot be null!");
        }

        // Validate receiverId
        if (request.getReceiverId() == null) {
            throw new InvalidInputException("Receiver ID cannot be null!");
        }

        // Validate amount
        if (request.getAmount() == null || request.getAmount() <= 0) {
            throw new InvalidInputException("Payment amount must be greater than 0!");
        }

        // Validate payment method
        if (request.getPaymentMethod() == null || request.getPaymentMethod().isEmpty()) {
            throw new InvalidInputException("Payment method cannot be empty!");
        }

        // Validate sender and receiver are different
        if (request.getSenderId().equals(request.getReceiverId())) {
            throw new InvalidInputException("Sender and receiver cannot be the same!");
        }

        Payment payment = new Payment();
        payment.setInvestmentId(request.getInvestmentId());
        payment.setSenderId(request.getSenderId());
        payment.setReceiverId(request.getReceiverId());
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(request.getPaymentMethod());

        Payment saved = paymentRepository.save(payment);
        logger.info("Payment processed successfully with id: {}", saved.getId());
        return mapToResponse(saved);
    }

    // Get payment by ID
    public PaymentResponse getPaymentById(Long id) {
        logger.info("Fetching payment with id: {}", id);
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Payment not found with id: {}", id);
                    return new ResourceNotFoundException("Payment not found with id: " + id);
                });
        return mapToResponse(payment);
    }

    // Get all payments
    public List<PaymentResponse> getAllPayments() {
        logger.info("Fetching all payments");
        return paymentRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Get payments by sender
    public List<PaymentResponse> getPaymentsBySender(Long senderId) {
        logger.info("Fetching payments for senderId: {}", senderId);
        return paymentRepository.findBySenderId(senderId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Get payments by receiver
    public List<PaymentResponse> getPaymentsByReceiver(Long receiverId) {
        logger.info("Fetching payments for receiverId: {}", receiverId);
        return paymentRepository.findByReceiverId(receiverId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Get payments by investment
    public List<PaymentResponse> getPaymentsByInvestment(Long investmentId) {
        logger.info("Fetching payments for investmentId: {}", investmentId);
        return paymentRepository.findByInvestmentId(investmentId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Update payment status to SUCCESS
    public PaymentResponse markSuccess(Long id) {
        logger.info("Marking payment as SUCCESS with id: {}", id);
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Payment not found with id: {}", id);
                    return new ResourceNotFoundException("Payment not found with id: " + id);
                });
        payment.setStatus("SUCCESS");
        Payment updated = paymentRepository.save(payment);

        // Publish event to RabbitMQ
        eventPublisher.publishPaymentSuccess(
            updated.getId(),
            updated.getInvestmentId(),
            updated.getSenderId(),
            updated.getReceiverId(),
            updated.getAmount()
        );

        logger.info("Payment marked as SUCCESS with id: {}", id);
        return mapToResponse(updated);
    }

    // Update payment status to FAILED
    public PaymentResponse markFailed(Long id) {
        logger.info("Marking payment as FAILED with id: {}", id);
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Payment not found with id: {}", id);
                    return new ResourceNotFoundException("Payment not found with id: " + id);
                });
        payment.setStatus("FAILED");
        Payment updated = paymentRepository.save(payment);
        logger.info("Payment marked as FAILED with id: {}", id);
        return mapToResponse(updated);
    }

    // Helper method
    private PaymentResponse mapToResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getInvestmentId(),
                payment.getSenderId(),
                payment.getReceiverId(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getPaymentMethod(),
                payment.getCreatedAt()
        );
    }
}