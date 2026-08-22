package com.founderlink.messaging;

import com.founderlink.messaging.dto.MessageRequest;
import com.founderlink.messaging.dto.MessageResponse;
import com.founderlink.messaging.entity.Message;
import com.founderlink.messaging.exception.InvalidInputException;
import com.founderlink.messaging.repository.MessageRepository;
import com.founderlink.messaging.service.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock private MessageRepository messageRepository;
    @InjectMocks private MessageService messageService;

    private Message message;
    private MessageRequest request;

    @BeforeEach
    void setUp() {
        message = new Message();
        message.setId(1L);
        message.setSenderId(1L);
        message.setReceiverId(2L);
        message.setContent("Hello!");
        message.setCreatedAt(LocalDateTime.now());

        request = new MessageRequest();
        request.setSenderId(1L);
        request.setReceiverId(2L);
        request.setContent("Hello!");
    }

    @Test
    void sendMessage_success() {
        when(messageRepository.save(any())).thenReturn(message);
        MessageResponse response = messageService.sendMessage(request);
        assertNotNull(response);
        assertEquals("Hello!", response.getContent());
        verify(messageRepository, times(1)).save(any());
    }

    @Test
    void sendMessage_nullSenderId_throwsInvalidInputException() {
        request.setSenderId(null);
        assertThrows(InvalidInputException.class,
                () -> messageService.sendMessage(request));
    }

    @Test
    void sendMessage_emptyContent_throwsInvalidInputException() {
        request.setContent("");
        assertThrows(InvalidInputException.class,
                () -> messageService.sendMessage(request));
    }

    @Test
    void sendMessage_senderEqualsReceiver_throwsInvalidInputException() {
        request.setReceiverId(1L);
        assertThrows(InvalidInputException.class,
                () -> messageService.sendMessage(request));
    }
}