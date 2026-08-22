package com.founderlink.team.event;

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

    // Publish Team Invite Sent event
    public void publishTeamInviteSent(Long teamId,
                                       Long startupId,
                                       Long invitedUserId,
                                       String role) {
        Map<String, Object> event = new HashMap<>();
        event.put("eventType", "TEAM_INVITE_SENT");
        event.put("teamId", teamId);
        event.put("startupId", startupId);
        event.put("userId", invitedUserId);
        event.put("message", "You have been invited to join startup: "
                + startupId + " as " + role);

        logger.info("Publishing TEAM_INVITE_SENT event for teamId: {}",
            teamId);
        rabbitTemplate.convertAndSend(EXCHANGE, "team.invite.sent", event);
        logger.info("TEAM_INVITE_SENT event published successfully");
    }
}