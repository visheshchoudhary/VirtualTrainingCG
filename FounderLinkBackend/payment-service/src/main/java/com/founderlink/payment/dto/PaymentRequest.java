package com.founderlink.payment.dto;

public class PaymentRequest {

    private Long investmentId;
    private Long senderId;
    private Long receiverId;
    private Double amount;
    private String paymentMethod;

    public Long getInvestmentId() { return investmentId; }
    public Long getSenderId() { return senderId; }
    public Long getReceiverId() { return receiverId; }
    public Double getAmount() { return amount; }
    public String getPaymentMethod() { return paymentMethod; }

    public void setInvestmentId(Long investmentId) { this.investmentId = investmentId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }
    public void setReceiverId(Long receiverId) { this.receiverId = receiverId; }
    public void setAmount(Double amount) { this.amount = amount; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
}