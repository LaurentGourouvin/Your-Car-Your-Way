package com.yourcaryourway.poc.chat;

import com.yourcaryourway.poc.model.Conversation;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * In-memory queue service for managing incoming support conversations.
 * Replaces AWS SQS for the POC — not suitable for production use.
 */
@Service
public class ConversationQueueService {
    private final Queue<Conversation> queue = new ConcurrentLinkedQueue<>();

    /**
     * Adds a conversation to the waiting queue.
     *
     * @param conversation the conversation to add
     */
    public void add(Conversation conversation) {
        queue.add(conversation);
    }

    /**
     * Returns all conversations currently waiting in the queue.
     *
     * @return a list of waiting conversations
     */
    public List<Conversation> getAll() {
        return queue.stream().toList();
    }

    /**
     * Removes a conversation from the queue when an agent takes it over.
     *
     * @param conversation the conversation to remove
     */
    public void remove(Conversation conversation) {
        queue.remove(conversation);
    }
}
