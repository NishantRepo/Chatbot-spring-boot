package com.nish.demo.controller;

import com.nish.demo.service.ChatService;
import com.nish.demo.service.Message;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Controller
public class ChatController {

    private final ChatService chatService;
    private final RestTemplate restTemplate;

    @Value("${python.service.url}")
    private String pythonServiceUrl;

    // We can use a simple counter for user IDs in this basic example.
    private static final AtomicInteger userIdCounter = new AtomicInteger(0);

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
        this.restTemplate = new RestTemplate();
    }

    @GetMapping("/chat")
    public String chatPage(Model model, HttpSession session) {
        System.out.println("in controller");
        // Assign a unique ID to the user if they don't have one.
        if (session.getAttribute("sessionId") == null) {
            session.setAttribute("sessionId", "user-" + userIdCounter.incrementAndGet());
        }

        String sessionId = (String) session.getAttribute("sessionId");
        List<Message> history = chatService.getChatHistory(sessionId);
        model.addAttribute("messages", history);
        return "chat";
    }

    @PostMapping("/send")
    public String sendMessage(@RequestParam String prompt, HttpSession session, RedirectAttributes redirectAttributes) {
        String sessionId = (String) session.getAttribute("sessionId");
        List<Message> history = chatService.getChatHistory(sessionId);

        // Add the user's message to the history.
        history.add(new Message("You", prompt));

        try {
            // Prepare the payload for the Python microservice.
            Map<String, String> payload = Map.of("prompt", prompt);

            // Send the request to the Python microservice to get the LLM response.
            String response = restTemplate.postForObject(pythonServiceUrl + "/generate", payload, String.class);

            // Parse the response (assuming it's a JSON string with a 'response' field).
            Map<String, Object> responseMap = restTemplate.postForObject(pythonServiceUrl + "/generate", payload, Map.class);
            String botResponse = (String) responseMap.get("response");

            // Add the bot's response to the history.
            history.add(new Message("ChatBot", botResponse));

        } catch (Exception e) {
            System.err.println("Error communicating with Python microservice: " + e.getMessage());
            history.add(new Message("ChatBot", "Sorry, I am currently unavailable."));
        }

        // Redirect back to the chat page to show the updated history.
        return "redirect:/";
    }
}
