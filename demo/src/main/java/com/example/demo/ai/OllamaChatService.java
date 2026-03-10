package com.example.demo.ai;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OllamaChatService {

    private static final int MAX_HISTORY_MESSAGES = 8;

    private final WebClient webClient;
    private final String model;
    private final Duration timeout;

    public OllamaChatService(
        WebClient.Builder webClientBuilder,
        @Value("${ollama.base-url:http://localhost:11434}") String baseUrl,
        @Value("${ollama.model:llama3.2:3b}") String model,
        @Value("${ollama.timeout-seconds:45}") long timeoutSeconds
    ) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
        this.model = model;
        this.timeout = Duration.ofSeconds(timeoutSeconds);
    }

    public String chat(String username, String message, List<ChatTurn> history, Map<String, Object> context) {
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", buildSystemPrompt(username, context)));

        for (ChatTurn turn : trimmedHistory(history)) {
            messages.add(Map.of("role", turn.role(), "content", turn.content()));
        }

        messages.add(Map.of("role", "user", "content", message));

        Map<String, Object> requestBody = new LinkedHashMap<>();
        requestBody.put("model", model);
        requestBody.put("stream", false);
        requestBody.put("options", Map.of("temperature", 0.4));
        requestBody.put("messages", messages);

        JsonNode response = webClient.post()
            .uri("/api/chat")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestBody)
            .retrieve()
            .bodyToMono(JsonNode.class)
            .block(timeout);

        if (response == null) {
            throw new IllegalStateException("Ollama returned an empty response.");
        }

        String content = response.path("message").path("content").asText("");
        if (content.isBlank()) {
            throw new IllegalStateException("Ollama response did not include assistant content.");
        }

        return content.trim();
    }

    private List<ChatTurn> trimmedHistory(List<ChatTurn> history) {
        if (history == null || history.isEmpty()) {
            return List.of();
        }

        int startIndex = Math.max(0, history.size() - MAX_HISTORY_MESSAGES);
        return history.subList(startIndex, history.size());
    }

    private String buildSystemPrompt(String username, Map<String, Object> context) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are the Eligibuddy assistant inside the user's dashboard.\n");
        prompt.append("Speak from the user's point of view and help them use Eligibuddy effectively.\n");
        prompt.append("Focus on eligibility guidance, form completion, interpreting results, prioritizing opportunities, and next steps.\n");
        prompt.append("Do not drift into developer-copilot mode unless the user explicitly asks about implementation.\n");
        prompt.append("Use recent conversation history for continuity when the user asks follow-up questions such as 'what next', 'what about that', or 'which one should I open first'.\n");
        prompt.append("Keep answers practical, short to medium length, and actionable.\n");
        prompt.append("When useful, use numbered steps or short bullet points.\n");
        prompt.append("Never invent opportunity counts; use only the context provided.\n");
        prompt.append("Current logged-in user: ").append(username).append("\n");

        String contextSummary = summarizeContext(context);
        if (!contextSummary.isBlank()) {
            prompt.append("Current Eligibuddy page context:\n");
            prompt.append(contextSummary).append("\n");
        }

        prompt.append("Always help the user move forward with their scholarships, schemes, exams, or jobs.\n");
        return prompt.toString();
    }

    private String summarizeContext(Map<String, Object> context) {
        Map<String, Object> profile = asMap(context.get("profile"));
        Map<String, Object> results = asMap(context.get("results"));
        List<String> lines = new ArrayList<>();

        String profileSummary = joinNonBlank(
            asText(profile.get("firstName")),
            asText(profile.get("lastName"))
        ).trim();

        if (!profileSummary.isBlank()) {
            lines.add("- Name: " + profileSummary);
        }

        addLineIfPresent(lines, "Age", profile.get("age"));
        addLineIfPresent(lines, "Gender", profile.get("gender"));
        addLineIfPresent(lines, "State", profile.get("state"));
        addLineIfPresent(lines, "District", profile.get("district"));
        addLineIfPresent(lines, "Qualification", profile.get("qualification"));
        addLineIfPresent(lines, "Category", profile.get("category"));
        addLineIfPresent(lines, "Income", profile.get("income"));
        addLineIfPresent(lines, "Field", profile.get("field"));
        addLineIfPresent(lines, "Disability", profile.get("disability"));

        Object preferences = profile.get("preferences");
        if (preferences instanceof List<?> list && !list.isEmpty()) {
            lines.add("- Preferences: " + list.stream().map(String::valueOf).collect(Collectors.joining(", ")));
        }

        if (!results.isEmpty()) {
            lines.add("- Current results: scholarships=" + asInt(results.get("scholarships"))
                + ", schemes=" + asInt(results.get("schemes"))
                + ", exams=" + asInt(results.get("exams"))
                + ", jobs=" + asInt(results.get("jobs")));
            addLineIfPresent(lines, "Active filter", results.get("currentFilter"));
        }

        return String.join("\n", lines);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> asMap(Object value) {
        if (value instanceof Map<?, ?> map) {
            return (Map<String, Object>) map;
        }
        return Map.of();
    }

    private void addLineIfPresent(List<String> lines, String label, Object value) {
        String text = asText(value);
        if (!text.isBlank()) {
            lines.add("- " + label + ": " + text);
        }
    }

    private String asText(Object value) {
        return value == null ? "" : String.valueOf(value).trim();
    }

    private int asInt(Object value) {
        if (value instanceof Number number) {
            return number.intValue();
        }
        try {
            return Integer.parseInt(asText(value));
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }

    private String joinNonBlank(String first, String second) {
        return List.of(first, second).stream()
            .filter(value -> value != null && !value.isBlank())
            .collect(Collectors.joining(" "));
    }
}
