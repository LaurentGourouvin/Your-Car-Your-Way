package com.yourcaryourway.poc.repository;

import com.yourcaryourway.poc.model.Conversation;
import com.yourcaryourway.poc.model.ConversationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, UUID> {
    List<Conversation> findByStatus(ConversationStatus status);
}
