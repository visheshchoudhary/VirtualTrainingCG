package com.founderlink.payment.event;

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

    // Publish Payment Success event
    public void publishPaymentSuccess(Long paymentId,
                                       Long investmentId,
                                       Long senderId,
                                       Long receiverId,
                                       Double amount) {
        Map<String, Object> event = new HashMap<>();
        event.put("eventType", "PAYMENT_SUCCESS");
        event.put("paymentId", paymentId);
        event.put("investmentId", investmentId);
        event.put("userId", receiverId);
        event.put("message", "Payment of amount: " + amount
                + " received successfully from userId: " + senderId);

        logger.info("Publishing PAYMENT_SUCCESS event for paymentId: {}",
            paymentId);
        rabbitTemplate.convertAndSend(EXCHANGE, "payment.success", event);
        logger.info("PAYMENT_SUCCESS event published successfully");
    }
}