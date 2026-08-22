package com.founderlink.team.dto;

public class TeamResponse {

    private Long id;
    private Long startupId;
    private Long userId;
    private String role;
    private String status;

    public TeamResponse(Long id, Long startupId, Long userId,
                        String role, String status) {
        this.id = id;
        this.startupId = startupId;
        this.userId = userId;
        this.role = role;
        this.status = status;
    }

    public Long getId() { return id; }
    public Long getStartupId() { return startupId; }
    public Long getUserId() { return userId; }
    public String getRole() { return role; }
    public String getStatus() { return status; }

    public void setId(Long id) { this.id = id; }
    public void setStartupId(Long startupId) { this.startupId = startupId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setRole(String role) { this.role = role; }
    public void setStatus(String status) { this.status = status; }
}