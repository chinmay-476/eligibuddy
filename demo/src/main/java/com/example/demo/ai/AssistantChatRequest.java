package com.example.demo.ai;

import java.util.Map;

public record AssistantChatRequest(String message, Map<String, Object> context) {

    public Map<String, Object> safeContext() {
        return context == null ? Map.of() : context;
    }
}
