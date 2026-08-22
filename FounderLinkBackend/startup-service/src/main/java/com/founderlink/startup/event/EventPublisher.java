package com.founderlink.startup.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class EventPublisher {

    private static final Logger logger =
        LoggerFactory.getLogger(EventPublisher.class);

    // Exchange name
    private static final String EXCHANGE = "founderlink.exchange";

    @Autowired
    private RabbitTemplate rabbitTemplate;

    // Publish Startup Created event
    public void publishStartupCreated(Long startupId,
                                       Long founderId,
                                       String industry,
                                       Double fundingGoal) {
        Map<String, Object> event = new HashMap<>();
        event.put("eventType", "STARTUP_CREATED");
        event.put("startupId", startupId);
        event.put("userId", founderId);
        event.put("message", "New startup created in industry: " + industry
                + " with funding goal: " + fundingGoal);

        logger.info("Publishing STARTUP_CREATED event for startupId: {}",
            startupId);
        rabbitTemplate.convertAndSend(EXCHANGE, "startup.created", event);
        logger.info("STARTUP_CREATED event published successfully");
    }
}