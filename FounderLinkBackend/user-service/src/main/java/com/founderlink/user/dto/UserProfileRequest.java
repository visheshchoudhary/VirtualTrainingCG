package com.founderlink.user.dto;

public class UserProfileRequest {

    private Long userId;
    private String name;
    private String email;
    private String bio;
    private String skills;
    private String experience;
    private String portfolioLink;
    private String role;

    public Long getUserId() { return userId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getBio() { return bio; }
    public String getSkills() { return skills; }
    public String getExperience() { return experience; }
    public String getPortfolioLink() { return portfolioLink; }
    public String getRole() { return role; }

    public void setUserId(Long userId) { this.userId = userId; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setBio(String bio) { this.bio = bio; }
    public void setSkills(String skills) { this.skills = skills; }
    public void setExperience(String experience) { this.experience = experience; }
    public void setPortfolioLink(String portfolioLink) { this.portfolioLink = portfolioLink; }
    public void setRole(String role) { this.role = role; }
}