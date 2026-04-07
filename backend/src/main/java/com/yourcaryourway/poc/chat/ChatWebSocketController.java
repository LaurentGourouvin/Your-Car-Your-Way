package com.yourcaryourway.poc.chat;

import com.yourcaryourway.poc.chat.dto.MessageResponse;
import com.yourcaryourway.poc.chat.dto.SendMessageRequest;
import com.yourcaryourway.poc.model.ChatMessage;
import com.yourcaryourway.poc.model.ChatMessageSender;
import com.yourcaryourway.poc.model.Conversation;
import com.yourcaryourway.poc.model.User;
import com.yourcaryourway.poc.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.UUID;

/**
 * WebSocket controller handling real-time chat messages.
 * Receives messages from clients and broadcasts them to conversation participants.
 */
@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {
    private final ConversationService conversationService;
    private final ChatMessageRepository chatMessageRepository;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Handles incoming chat messages from WebSocket clients.
     * Persists the message to the database and broadcasts it to all
     * subscribers of the conversation topic.
     *
     * @param conversationId the ID of the conversation
     * @param request the message request containing the content
     * @param principal the authenticated user sending the message
     */
    @MessageMapping("/chat/{conversationId}")
    public void sendMessage(@DestinationVariable String conversationId, SendMessageRequest request, Principal principal) {
        UUID id = UUID.fromString(conversationId);
        User user = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        Conversation conversation = conversationService.getConversation(id);

        ChatMessage message = new ChatMessage();
        message.setConversation(conversation);
        message.setSender(user);
        message.setSenderType(this.isSupport(user) ? ChatMessageSender.SUPPORT : ChatMessageSender.CLIENT);
        message.setContent(request.getContent());

        chatMessageRepository.save(message);
        messagingTemplate.convertAndSend(
                "/topic/conversation/" + conversationId,
                new MessageResponse(
                        message.getId(),
                        message.getSender().getId(),
                        message.getSenderType(),
                        message.getContent(),
                        message.getSentAt()
                )
        );
    }

    /**
     * Checks whether a user has the SUPPORT role.
     *
     * @param user the user to check
     * @return true if the user is a support agent, false otherwise
     */
    private boolean isSupport(User user) {
        return user.getRoles().stream().anyMatch(role -> role.getName().equals("SUPPORT"));
    }
}
