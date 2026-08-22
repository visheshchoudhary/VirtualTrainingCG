package com.founderlink.notification.dto;

import java.io.Serializable;

public class NotificationEvent implements Serializable {

    private String eventType;
    private String message;
    private Long userId;

    public NotificationEvent() {}

    public NotificationEvent(String eventType, String message, Long userId) {
        this.eventType = eventType;
        this.message = message;
        this.userId = userId;
    }

    public String getEventType() { return eventType; }
    public String getMessage() { return message; }
    public Long getUserId() { return userId; }

    public void setEventType(String eventType) { this.eventType = eventType; }
    public void setMessage(String message) { this.message = message; }
    public void setUserId(Long userId) { this.userId = userId; }
}