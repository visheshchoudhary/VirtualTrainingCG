package com.founderlink.notification.dto;

import java.time.LocalDateTime;

public class NotificationResponse {

    private Long id;
    private String eventType;
    private String message;
    private Long userId;
    private boolean isRead;
    private LocalDateTime createdAt;

    public NotificationResponse(Long id, String eventType, String message,
                                Long userId, boolean isRead,
                                LocalDateTime createdAt) {
        this.id = id;
        this.eventType = eventType;
        this.message = message;
        this.userId = userId;
        this.isRead = isRead;
        this.createdAt = createdAt;
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