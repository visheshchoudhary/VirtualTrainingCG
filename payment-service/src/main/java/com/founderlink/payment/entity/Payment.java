package com.founderlink.payment.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long investmentId;
    private Long senderId;
    private Long receiverId;
    private Double amount;
    private String status;
    private String paymentMethod;
    private LocalDateTime createdAt;

    // Status values: PENDING, SUCCESS, FAILED
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.status = "PENDING";
    }

    public Long getId() { return id; }
    public Long getInvestmentId() { return investmentId; }
    public Long getSenderId() { return senderId; }
    public Long getReceiverId() { return receiverId; }
    public Double getAmount() { return amount; }
    public String getStatus() { return status; }
    public String getPaymentMethod() { return paymentMethod; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setInvestmentId(Long investmentId) { this.investmentId = investmentId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }
    public void setReceiverId(Long receiverId) { this.receiverId = receiverId; }
    public void setAmount(Double amount) { this.amount = amount; }
    public void setStatus(String status) { this.status = status; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}