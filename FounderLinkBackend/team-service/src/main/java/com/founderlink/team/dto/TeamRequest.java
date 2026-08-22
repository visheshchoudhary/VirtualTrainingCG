package com.founderlink.team.dto;

public class TeamRequest {

    private Long startupId;
    private Long userId;
    private String role;

    public Long getStartupId() { return startupId; }
    public Long getUserId() { return userId; }
    public String getRole() { return role; }

    public void setStartupId(Long startupId) { this.startupId = startupId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setRole(String role) { this.role = role; }
}