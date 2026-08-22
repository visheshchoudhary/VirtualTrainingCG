package com.founderlink.notification.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String eventType;
    private String message;
    private Long userId;
    private boolean isRead;
    private LocalDateTime createdAt;

    // EventType values:
    // STARTUP_CREATED, INVESTMENT_CREATED, TEAM_INVITE_SENT, PAYMENT_SUCCESS

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.isRead = false;
    }

    public Long getId() { return id; }
    public String getEventType() { return eventType; }
    public String getMessage() { return message; }
    public Long getUserId() { return userId; }
    public boolean isRead() { return isRead; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public void setMessage(String message) { this.message = message; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setRead(boolean isRead) { this.isRead = isRead; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}