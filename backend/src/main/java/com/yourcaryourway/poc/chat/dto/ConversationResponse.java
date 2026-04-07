package com.yourcaryourway.poc.chat.dto;

import com.yourcaryourway.poc.model.ConversationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class ConversationResponse {
    private UUID id;
    private UUID userId;
    private String userFirstName;
    private String userLastName;
    private UUID agentId;
    private ConversationStatus status;
    private LocalDateTime createdAt;
}
