package com.founderlink.notification.service;

import com.founderlink.notification.dto.NotificationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.founderlink.notification.entity.Notification;
import com.founderlink.notification.exception.ResourceNotFoundException;
import com.founderlink.notification.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    // Logger
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private NotificationRepository notificationRepository;

    // Get all notifications for a user
    public List<NotificationResponse> getNotificationsByUser(Long userId) {
        logger.info("Fetching notifications for userId: {}", userId);
        return notificationRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Get unread notifications for a user
    public List<NotificationResponse> getUnreadNotifications(Long userId) {
        logger.info("Fetching unread notifications for userId: {}", userId);
        return notificationRepository.findByUserIdAndIsRead(userId, false)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Mark notification as read
    public NotificationResponse markAsRead(Long id) {
        logger.info("Marking notification as read with id: {}", id);
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Notification not found with id: {}", id);
                    return new ResourceNotFoundException(
                        "Notification not found with id: " + id);
                });
        notification.setRead(true);
        Notification updated = notificationRepository.save(notification);
        logger.info("Notification marked as read with id: {}", id);
        return mapToResponse(updated);
    }

    // Get all notifications
    public Page<NotificationResponse> getAllNotifications(Pageable pageable) {
        logger.info("Fetching all notifications");
        return notificationRepository.findAll(pageable).map(this::mapToResponse);
    }


    // Get notifications by event type
    public List<NotificationResponse> getByEventType(String eventType) {
        logger.info("Fetching notifications by eventType: {}", eventType);
        return notificationRepository.findByEventType(eventType)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Helper method
    private NotificationResponse mapToResponse(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getEventType(),
                notification.getMessage(),
                notification.getUserId(),
                notification.isRead(),
                notification.getCreatedAt()
        );
    }
}