package com.founderlink.team.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "team")
public class TeamMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long startupId;
    private Long userId;
    private String role;
    private String status;

    // Role values: CTO, CPO, MARKETING_HEAD, ENGINEERING_LEAD
    // Status values: PENDING, ACCEPTED

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