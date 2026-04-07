package com.yourcaryourway.poc.chat;

import com.yourcaryourway.poc.chat.dto.ConversationResponse;
import com.yourcaryourway.poc.chat.dto.MessageResponse;
import com.yourcaryourway.poc.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller handling conversation management.
 * Provides endpoints for creating, managing and retrieving support conversations.
 */
@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
public class ConversationController {
    private final ConversationService conversationService;

    /**
     * Creates a new support conversation for the authenticated user.
     *
     * @param currentUser the authenticated client opening the conversation
     * @return a 201 Created response with the created conversation details
     */
    @PostMapping
    public ResponseEntity<ConversationResponse> createConversation(@AuthenticationPrincipal User currentUser){
        return ResponseEntity.status(201).body(conversationService.createConversation(currentUser));
    }

    /**
     * Returns all conversations currently waiting in the queue.
     * Only accessible by agents with the SUPPORT role.
     *
     * @return a 200 OK response with the list of waiting conversations
     */
    @GetMapping("/queue")
    public ResponseEntity<List<ConversationResponse>> getQueue() {
        return ResponseEntity.ok(conversationService.getQueue());
    }

    /**
     * Assigns the authenticated agent to a waiting conversation.
     * Changes the conversation status from WAITING to IN_PROGRESS.
     *
     * @param id the ID of the conversation to take over
     * @param agent the authenticated agent taking the conversation
     * @return a 200 OK response with the updated conversation details
     */
    @PatchMapping("/{id}/take")
    public ResponseEntity<ConversationResponse> takeConversation(@PathVariable UUID id, @AuthenticationPrincipal User agent) {
        return ResponseEntity.ok(conversationService.takeConversation(id, agent));
    }

    /**
     * Closes a conversation and marks the agent as available again.
     *
     * @param id the ID of the conversation to close
     * @param agent the authenticated agent closing the conversation
     * @return a 200 OK response with the updated conversation details
     */
    @PatchMapping("/{id}/close")
    public ResponseEntity<ConversationResponse> closeConversation(@PathVariable UUID id, @AuthenticationPrincipal User agent) {
        return ResponseEntity.ok(conversationService.closeConversation(id, agent));
    }

    /**
     * Returns the message history of a conversation.
     * Accessible by the conversation owner (CLIENT) or any SUPPORT agent.
     *
     * @param id the ID of the conversation
     * @return a 200 OK response with the list of messages
     */
    @GetMapping("/{id}/messages")
    public ResponseEntity<List<MessageResponse>> getMessages(@PathVariable UUID id) {
        return ResponseEntity.ok(conversationService.getMessages(id));
    }
}
