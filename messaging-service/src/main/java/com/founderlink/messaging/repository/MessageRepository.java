package com.founderlink.messaging.repository;

import com.founderlink.messaging.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySenderIdAndReceiverId(Long senderId, Long receiverId);
    List<Message> findBySenderId(Long senderId);
    List<Message> findByReceiverId(Long receiverId);
}