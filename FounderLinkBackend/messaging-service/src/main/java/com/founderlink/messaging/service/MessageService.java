package com.founderlink.messaging.service;

import com.founderlink.messaging.dto.MessageRequest;
import com.founderlink.messaging.dto.MessageResponse;
import com.founderlink.messaging.entity.Message;
import com.founderlink.messaging.exception.InvalidInputException;
import com.founderlink.messaging.repository.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService {

    // Logger
    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);

    @Autowired
    private MessageRepository messageRepository;

    // Send message
    public MessageResponse sendMessage(MessageRequest request) {

        logger.info("Sending message from userId: {} to userId: {}",
                request.getSenderId(), request.getReceiverId());

        // Validate senderId
        if (request.getSenderId() == null) {
            throw new InvalidInputException("Sender ID cannot be null!");
        }

        // Validate receiverId
        if (request.getReceiverId() == null) {
            throw new InvalidInputException("Receiver ID cannot be null!");
        }

        // Validate content
        if (request.getContent() == null || request.getContent().isEmpty()) {
            throw new InvalidInputException("Message content cannot be empty!");
        }

        // Validate sender and receiver are different
        if (request.getSenderId().equals(request.getReceiverId())) {
            throw new InvalidInputException("Sender and receiver cannot be the same!");
        }

        Message message = new Message();
        message.setSenderId(request.getSenderId());
        message.setReceiverId(request.getReceiverId());
        message.setContent(request.getContent());

        Message saved = messageRepository.save(message);
        logger.info("Message sent successfully with id: {}", saved.getId());
        return mapToResponse(saved);
    }

    // Get conversation between two users
    public List<MessageResponse> getConversation(Long senderId, Long receiverId) {
        logger.info("Fetching conversation between {} and {}", senderId, receiverId);
        List<Message> sent = messageRepository
                .findBySenderIdAndReceiverId(senderId, receiverId);
        List<Message> received = messageRepository
                .findBySenderIdAndReceiverId(receiverId, senderId);
        sent.addAll(received);
        return sent.stream()
                .sorted((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt()))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Get all messages sent by user
    public List<MessageResponse> getSentMessages(Long senderId) {
        logger.info("Fetching sent messages for userId: {}", senderId);
        return messageRepository.findBySenderId(senderId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Get all messages received by user
    public List<MessageResponse> getReceivedMessages(Long receiverId) {
        logger.info("Fetching received messages for userId: {}", receiverId);
        return messageRepository.findByReceiverId(receiverId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Get all messages
    public List<MessageResponse> getAllMessages() {
        logger.info("Fetching all messages");
        return messageRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Helper method
    private MessageResponse mapToResponse(Message message) {
        return new MessageResponse(
                message.getId(),
                message.getSenderId(),
                message.getReceiverId(),
                message.getContent(),
                message.getCreatedAt()
        );
    }
}