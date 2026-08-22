package com.founderlink.startup.dto;

public class StartupRequest {

    private String name;
    private String description;
    private String industry;
    private String problemStatement;
    private String solution;
    private Double fundingGoal;
    private String stage;
    private Long founderId;

    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getIndustry() { return industry; }
    public String getProblemStatement() { return problemStatement; }
    public String getSolution() { return solution; }
    public Double getFundingGoal() { return fundingGoal; }
    public String getStage() { return stage; }
    public Long getFounderId() { return founderId; }

    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setIndustry(String industry) { this.industry = industry; }
    public void setProblemStatement(String problemStatement) { this.problemStatement = problemStatement; }
    public void setSolution(String solution) { this.solution = solution; }
    public void setFundingGoal(Double fundingGoal) { this.fundingGoal = fundingGoal; }
    public void setStage(String stage) { this.stage = stage; }
    public void setFounderId(Long founderId) { this.founderId = founderId; }
}