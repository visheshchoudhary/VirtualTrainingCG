package com.founderlink.notification;

import com.founderlink.notification.dto.NotificationResponse;
import com.founderlink.notification.entity.Notification;
import com.founderlink.notification.exception.ResourceNotFoundException;
import com.founderlink.notification.repository.NotificationRepository;
import com.founderlink.notification.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock private NotificationRepository notificationRepository;
    @InjectMocks private NotificationService notificationService;

    private Notification notification;

    @BeforeEach
    void setUp() {
        notification = new Notification();
        notification.setId(1L);
        notification.setUserId(1L);
        notification.setEventType("STARTUP_CREATED");
        notification.setMessage("New startup posted!");
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void getNotificationsByUser_returnsList() {
        when(notificationRepository.findByUserId(1L))
                .thenReturn(List.of(notification));
        List<NotificationResponse> result =
                notificationService.getNotificationsByUser(1L);
        assertEquals(1, result.size());
        assertEquals("STARTUP_CREATED", result.get(0).getEventType());
    }

    @Test
    void getUnreadNotifications_returnsList() {
        when(notificationRepository.findByUserIdAndIsRead(1L, false))
                .thenReturn(List.of(notification));
        List<NotificationResponse> result =
                notificationService.getUnreadNotifications(1L);
        assertEquals(1, result.size());
        assertFalse(result.get(0).isRead());
    }

    @Test
    void markAsRead_success() {
        when(notificationRepository.findById(1L))
                .thenReturn(Optional.of(notification));
        when(notificationRepository.save(any())).thenReturn(notification);

        NotificationResponse response = notificationService.markAsRead(1L);
        assertNotNull(response);
        verify(notificationRepository, times(1)).save(any());
    }

    @Test
    void markAsRead_notFound_throwsResourceNotFoundException() {
        when(notificationRepository.findById(99L))
                .thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> notificationService.markAsRead(99L));
    }

    @Test
    void getAllNotifications_paginated() {
        Pageable pageable = PageRequest.of(0, 20);
        when(notificationRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(List.of(notification)));

        Page<NotificationResponse> page =
                notificationService.getAllNotifications(pageable);

        assertEquals(1, page.getTotalElements());
        assertEquals("STARTUP_CREATED",
                page.getContent().get(0).getEventType());
    }
}
