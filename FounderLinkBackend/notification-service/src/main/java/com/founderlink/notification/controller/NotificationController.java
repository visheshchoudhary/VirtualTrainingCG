package com.founderlink.notification.controller;

import com.founderlink.notification.dto.NotificationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import com.founderlink.notification.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/notifications")
public class NotificationController {

    // Logger
    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);

    @Autowired
    private NotificationService notificationService;

    // Get all notifications
    @GetMapping
    public ResponseEntity<Page<NotificationResponse>> getAllNotifications(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        logger.info("GET /notifications called");
        return ResponseEntity.ok(notificationService.getAllNotifications(pageable));
    }


    // Get notifications by user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationResponse>> getByUser(
            @PathVariable Long userId) {
        logger.info("GET /notifications/user/{} called", userId);
        return ResponseEntity.ok(notificationService.getNotificationsByUser(userId));
    }

    // Get unread notifications by user
    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<NotificationResponse>> getUnread(
            @PathVariable Long userId) {
        logger.info("GET /notifications/user/{}/unread called", userId);
        return ResponseEntity.ok(notificationService.getUnreadNotifications(userId));
    }

    // Mark notification as read
    @PutMapping("/{id}/read")
    public ResponseEntity<NotificationResponse> markAsRead(
            @PathVariable Long id) {
        logger.info("PUT /notifications/{}/read called", id);
        return ResponseEntity.ok(notificationService.markAsRead(id));
    }

    // Get notifications by event type
    @GetMapping("/event/{eventType}")
    public ResponseEntity<List<NotificationResponse>> getByEventType(
            @PathVariable String eventType) {
        logger.info("GET /notifications/event/{} called", eventType);
        return ResponseEntity.ok(notificationService.getByEventType(eventType));
    }
}