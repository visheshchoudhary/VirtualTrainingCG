package com.founderlink.investment.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class EventPublisher {

    private static final Logger logger =
        LoggerFactory.getLogger(EventPublisher.class);

    private static final String EXCHANGE = "founderlink.exchange";

    @Autowired
    private RabbitTemplate rabbitTemplate;

    // Publish Investment Created event
    public void publishInvestmentCreated(Long investmentId,
                                          Long startupId,
                                          Long investorId,
                                          Double amount) {
        Map<String, Object> event = new HashMap<>();
        event.put("eventType", "INVESTMENT_CREATED");
        event.put("investmentId", investmentId);
        event.put("startupId", startupId);
        event.put("userId", investorId);
        event.put("message", "New investment of amount: " + amount
                + " received for startupId: " + startupId);

        logger.info("Publishing INVESTMENT_CREATED event for investmentId: {}",
            investmentId);
        rabbitTemplate.convertAndSend(EXCHANGE, "investment.created", event);
        logger.info("INVESTMENT_CREATED event published successfully");
    }
}