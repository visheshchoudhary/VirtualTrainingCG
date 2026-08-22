package com.founderlink.user.dto;

public class UserProfileResponse {

    private Long id;
    private Long userId;
    private String name;
    private String email;
    private String bio;
    private String skills;
    private String experience;
    private String portfolioLink;
    private String role;

    public UserProfileResponse(Long id, Long userId, String name, String email,
                                String bio, String skills, String experience,
                                String portfolioLink, String role) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.bio = bio;
        this.skills = skills;
        this.experience = experience;
        this.portfolioLink = portfolioLink;
        this.role = role;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getBio() { return bio; }
    public String getSkills() { return skills; }
    public String getExperience() { return experience; }
    public String getPortfolioLink() { return portfolioLink; }
    public String getRole() { return role; }

    public void setId(Long id) { this.id = id; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setBio(String bio) { this.bio = bio; }
    public void setSkills(String skills) { this.skills = skills; }
    public void setExperience(String experience) { this.experience = experience; }
    public void setPortfolioLink(String portfolioLink) { this.portfolioLink = portfolioLink; }
    public void setRole(String role) { this.role = role; }
}