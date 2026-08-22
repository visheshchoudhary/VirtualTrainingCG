package com.founderlink.investment.dto;

public class InvestmentRequest {

    private Long startupId;
    private Long investorId;
    private Double amount;
    private String status;

    public Long getStartupId() { return startupId; }
    public Long getInvestorId() { return investorId; }
    public Double getAmount() { return amount; }
    public String getStatus() { return status; }

    public void setStartupId(Long startupId) { this.startupId = startupId; }
    public void setInvestorId(Long investorId) { this.investorId = investorId; }
    public void setAmount(Double amount) { this.amount = amount; }
    public void setStatus(String status) { this.status = status; }
}