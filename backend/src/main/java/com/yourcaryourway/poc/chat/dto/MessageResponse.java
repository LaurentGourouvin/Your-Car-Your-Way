package com.yourcaryourway.poc.chat.dto;

import com.yourcaryourway.poc.model.ChatMessageSender;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class MessageResponse {
    private UUID id;
    private UUID senderId;
    private ChatMessageSender senderType;
    private String content;
    private LocalDateTime sentAt;
}
