package com.founderlink.investment.dto;

public class InvestmentResponse {

    private Long id;
    private Long startupId;
    private Long investorId;
    private Double amount;
    private String status;

    public InvestmentResponse(Long id, Long startupId, Long investorId,
                               Double amount, String status) {
        this.id = id;
        this.startupId = startupId;
        this.investorId = investorId;
        this.amount = amount;
        this.status = status;
    }

    public Long getId() { return id; }
    public Long getStartupId() { return startupId; }
    public Long getInvestorId() { return investorId; }
    public Double getAmount() { return amount; }
    public String getStatus() { return status; }

    public void setId(Long id) { this.id = id; }
    public void setStartupId(Long startupId) { this.startupId = startupId; }
    public void setInvestorId(Long investorId) { this.investorId = investorId; }
    public void setAmount(Double amount) { this.amount = amount; }
    public void setStatus(String status) { this.status = status; }
}