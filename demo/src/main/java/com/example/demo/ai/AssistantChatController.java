package com.example.demo.ai;

import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class AssistantChatController {

    private static final Logger log = LoggerFactory.getLogger(AssistantChatController.class);
    private static final String CHAT_HISTORY_KEY = "eligibuddyAssistantHistory";
    private static final int MAX_HISTORY_SIZE = 12;

    private final OllamaChatService ollamaChatService;
    private final AssistantFallbackService fallbackService;

    public AssistantChatController(OllamaChatService ollamaChatService, AssistantFallbackService fallbackService) {
        this.ollamaChatService = ollamaChatService;
        this.fallbackService = fallbackService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> chat(
        @RequestBody AssistantChatRequest request,
        Authentication authentication,
        HttpSession session
    ) {
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Please log in to use the Eligibuddy assistant."));
        }

        String message = request.message() == null ? "" : request.message().trim();
        if (message.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Message cannot be empty."));
        }

        List<ChatTurn> history = getHistory(session);
        Map<String, Object> context = request.safeContext();
        String response;
        String source = "ollama";

        try {
            response = ollamaChatService.chat(authentication.getName(), message, history, context);
        } catch (Exception exception) {
            log.warn("Ollama assistant unavailable, using contextual fallback: {}", exception.getMessage());
            response = fallbackService.generateResponse(authentication.getName(), message, history, context);
            source = "fallback";
        }

        appendTurn(history, new ChatTurn("user", message));
        appendTurn(history, new ChatTurn("assistant", response));
        session.setAttribute(CHAT_HISTORY_KEY, history);

        return ResponseEntity.ok(Map.of("response", response, "source", source));
    }

    private List<ChatTurn> getHistory(HttpSession session) {
        Object stored = session.getAttribute(CHAT_HISTORY_KEY);
        if (stored instanceof List<?> list) {
            List<ChatTurn> history = new ArrayList<>();
            for (Object item : list) {
                if (item instanceof ChatTurn turn) {
                    history.add(turn);
                } else if (item instanceof Map<?, ?> map) {
                    Object role = map.get("role");
                    Object content = map.get("content");
                    history.add(new ChatTurn(
                        role == null ? "user" : String.valueOf(role),
                        content == null ? "" : String.valueOf(content)
                    ));
                }
            }
            return history;
        }
        return new ArrayList<>();
    }

    private void appendTurn(List<ChatTurn> history, ChatTurn turn) {
        history.add(turn);
        while (history.size() > MAX_HISTORY_SIZE) {
            history.remove(0);
        }
    }
}
