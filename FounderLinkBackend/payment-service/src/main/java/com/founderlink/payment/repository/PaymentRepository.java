package com.founderlink.payment.repository;

import com.founderlink.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findBySenderId(Long senderId);
    List<Payment> findByReceiverId(Long receiverId);
    List<Payment> findByInvestmentId(Long investmentId);
    List<Payment> findByStatus(String status);
}