package com.founderlink.notification.listener;

import com.founderlink.notification.config.RabbitMQConfig;
import com.founderlink.notification.dto.NotificationEvent;
import com.founderlink.notification.entity.Notification;
import com.founderlink.notification.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NotificationListener {

    // Logger
    private static final Logger logger = LoggerFactory.getLogger(NotificationListener.class);

    @Autowired
    private NotificationRepository notificationRepository;

    // Listen to Startup Created events
    @RabbitListener(queues = RabbitMQConfig.STARTUP_QUEUE)
    public void handleStartupCreated(NotificationEvent event) {
        logger.info("Received STARTUP_CREATED event for userId: {}", event.getUserId());
        saveNotification(event);
        logger.info("Notification saved for STARTUP_CREATED event");
    }

    // Listen to Investment Created events
    @RabbitListener(queues = RabbitMQConfig.INVESTMENT_QUEUE)
    public void handleInvestmentCreated(NotificationEvent event) {
        logger.info("Received INVESTMENT_CREATED event for userId: {}", event.getUserId());
        saveNotification(event);
        logger.info("Notification saved for INVESTMENT_CREATED event");
    }

    // Listen to Team Invite events
    @RabbitListener(queues = RabbitMQConfig.TEAM_QUEUE)
    public void handleTeamInvite(NotificationEvent event) {
        logger.info("Received TEAM_INVITE_SENT event for userId: {}", event.getUserId());
        saveNotification(event);
        logger.info("Notification saved for TEAM_INVITE_SENT event");
    }

    // Listen to Payment Success events
    @RabbitListener(queues = RabbitMQConfig.PAYMENT_QUEUE)
    public void handlePaymentSuccess(NotificationEvent event) {
        logger.info("Received PAYMENT_SUCCESS event for userId: {}", event.getUserId());
        saveNotification(event);
        logger.info("Notification saved for PAYMENT_SUCCESS event");
    }

    // Save notification to database
    private void saveNotification(NotificationEvent event) {
        Notification notification = new Notification();
        notification.setEventType(event.getEventType());
        notification.setMessage(event.getMessage());
        notification.setUserId(event.getUserId());
        notificationRepository.save(notification);
    }
}