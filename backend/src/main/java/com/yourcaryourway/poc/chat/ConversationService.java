package com.yourcaryourway.poc.chat;

import com.yourcaryourway.poc.chat.dto.ConversationResponse;
import com.yourcaryourway.poc.chat.dto.MessageResponse;
import com.yourcaryourway.poc.model.AgentProfile;
import com.yourcaryourway.poc.model.Conversation;
import com.yourcaryourway.poc.model.ConversationStatus;
import com.yourcaryourway.poc.model.User;
import com.yourcaryourway.poc.repository.AgentProfileRepository;
import com.yourcaryourway.poc.repository.ChatMessageRepository;
import com.yourcaryourway.poc.repository.ConversationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConversationService {
    private final ConversationRepository conversationRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final AgentProfileRepository agentProfileRepository;
    private final ConversationQueueService conversationQueueService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Creates a new support conversation for the given user.
     * Adds the conversation to the in-memory queue and notifies agents via WebSocket.
     *
     * @param user the authenticated user opening the conversation
     * @return a {@link ConversationResponse} with the created conversation details
     */
    public ConversationResponse createConversation(User user) {
        Conversation conversation = new Conversation();
        conversation.setUser(user);
        conversation.setCreatedAt(LocalDateTime.now());
        conversation.setStatus(ConversationStatus.WAITING);

        conversationRepository.save(conversation);

        conversationQueueService.add(conversation);
        messagingTemplate.convertAndSend("/topic/queue", "new_conversation");

        return toConversationResponse(conversation);
    }

    /**
     * Returns all conversations currently waiting in the queue.
     *
     * @return a list of {@link ConversationResponse} with status WAITING
     */
    public List<ConversationResponse> getQueue() {
        return conversationQueueService.getAll()
                .stream()
                .map(this::toConversationResponse)
                .toList();
    }

    /**
     * Assigns an agent to a waiting conversation and marks it as IN_PROGRESS.
     * Removes the conversation from the queue and sets the agent as unavailable.
     *
     * @param conversationId the ID of the conversation to take over
     * @param agent the authenticated agent taking the conversation
     * @return a {@link ConversationResponse} with the updated conversation details
     * @throws RuntimeException if the conversation is not found
     * @throws RuntimeException if the conversation status is not WAITING
     * @throws RuntimeException if the agent profile is not found
     */
    public ConversationResponse takeConversation(UUID conversationId, User agent) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found."));

        if(conversation.getStatus() != ConversationStatus.WAITING) {
            throw new RuntimeException("Conversation should have status: WAITING to be taken.");
        }

        conversation.setAgent(agent);
        conversation.setStatus(ConversationStatus.IN_PROGRESS);
        conversationQueueService.remove(conversation);

        AgentProfile agentToUpdate = agentProfileRepository.findById(agent.getId())
                .orElseThrow(() -> new RuntimeException("Agent not found."));

        agentToUpdate.setAvailable(false);

        conversationRepository.save(conversation);
        agentProfileRepository.save(agentToUpdate);

        return toConversationResponse(conversation);
    }

    /**
     * Closes a conversation and marks the agent as available again.
     *
     * @param conversationId the ID of the conversation to close
     * @param agent the authenticated agent closing the conversation
     * @return a {@link ConversationResponse} with the updated conversation details
     * @throws RuntimeException if the conversation is not found
     * @throws RuntimeException if the agent profile is not found
     */
    public ConversationResponse closeConversation(UUID conversationId, User agent) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found."));

        conversation.setStatus(ConversationStatus.CLOSED);

        AgentProfile agentProfile = agentProfileRepository.findById(agent.getId())
                .orElseThrow(() -> new RuntimeException("Agent not found."));

        agentProfile.setAvailable(true);

        conversationRepository.save(conversation);
        agentProfileRepository.save(agentProfile);

        return toConversationResponse(conversation);
    }

    /**
     * Returns all messages from a given conversation ordered by sent date.
     *
     * @param conversationId the ID of the conversation
     * @return a list of {@link MessageResponse} containing the message history
     */
    public List<MessageResponse> getMessages(UUID conversationId) {
        return chatMessageRepository.findByConversationId(conversationId)
                .stream()
                .map(message -> new MessageResponse(
                        message.getId(),
                        message.getSender().getId(),
                        message.getSenderType(),
                        message.getContent(),
                        message.getSentAt()
                ))
                .toList();
    }

    /**
     * Retrieves a conversation by its ID.
     *
     * @param conversationId the ID of the conversation to retrieve
     * @return the {@link Conversation} entity
     * @throws RuntimeException if the conversation is not found
     */
    public Conversation getConversation(UUID conversationId) {
        return conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found."));
    }

    /**
     * Converts a {@link Conversation} entity to a {@link ConversationResponse} DTO.
     *
     * @param conversation the conversation entity to convert
     * @return the corresponding {@link ConversationResponse}
     */
    private ConversationResponse toConversationResponse(Conversation conversation) {
        return new ConversationResponse(
                conversation.getId(),
                conversation.getUser().getId(),
                conversation.getUser().getFirstname(),
                conversation.getUser().getLastname(),
                conversation.getAgent() != null ? conversation.getAgent().getId() : null,
                conversation.getStatus(),
                conversation.getCreatedAt()
        );
    }
}
