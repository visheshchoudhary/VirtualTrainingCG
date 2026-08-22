package com.founderlink.messaging.controller;

import com.founderlink.messaging.dto.MessageRequest;
import com.founderlink.messaging.dto.MessageResponse;
import com.founderlink.messaging.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    // Send message
    @PostMapping
    public ResponseEntity<MessageResponse> sendMessage(
            @RequestBody MessageRequest request) {
        return ResponseEntity.ok(messageService.sendMessage(request));
    }

    // Get conversation between two users
    @GetMapping("/conversation")
    public ResponseEntity<List<MessageResponse>> getConversation(
            @RequestParam Long senderId,
            @RequestParam Long receiverId) {
        return ResponseEntity.ok(messageService.getConversation(senderId, receiverId));
    }

    // Get sent messages
    @GetMapping("/sent/{senderId}")
    public ResponseEntity<List<MessageResponse>> getSentMessages(
            @PathVariable Long senderId) {
        return ResponseEntity.ok(messageService.getSentMessages(senderId));
    }

    // Get received messages
    @GetMapping("/received/{receiverId}")
    public ResponseEntity<List<MessageResponse>> getReceivedMessages(
            @PathVariable Long receiverId) {
        return ResponseEntity.ok(messageService.getReceivedMessages(receiverId));
    }

    // Get all messages
    @GetMapping
    public ResponseEntity<List<MessageResponse>> getAllMessages() {
        return ResponseEntity.ok(messageService.getAllMessages());
    }
}