package com.nish.demo.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ChatService {

    // Using a ConcurrentHashMap to handle multiple user sessions safely.
    // The key is the session ID, and the value is the chat history for that session.
    private final Map<String, List<Message>> chatSessions = new ConcurrentHashMap<>();

    /**
     * Retrieves the chat history for a specific user session.
     * If no history exists, a new one is created.
     *
     * @param sessionId The unique ID for the user's session.
     * @return The list of messages for that session.
     */
    public List<Message> getChatHistory(String sessionId) {
        return chatSessions.computeIfAbsent(sessionId, k -> new ArrayList<>());
    }
}
