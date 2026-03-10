package com.example.demo.opportunity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class OpportunityCriteriaNormalizer {

    private final ObjectMapper objectMapper;

    public OpportunityCriteriaNormalizer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Nullable
    public String normalizeListCriteria(@Nullable String value) {
        String trimmed = trimToNull(value);
        if (trimmed == null) {
            return null;
        }

        try {
            if (trimmed.startsWith("[")) {
                List<String> parsedValues = objectMapper.readValue(trimmed, new TypeReference<List<String>>() {});
                return writeListValues(parsedValues);
            }
        } catch (JsonProcessingException ignored) {
            // Fall back to comma-separated parsing for human-friendly admin input.
        }

        List<String> parsedValues = List.of(trimmed.split("[,\\n]"))
                .stream()
                .map(this::trimToNull)
                .filter(item -> item != null)
                .distinct()
                .collect(Collectors.toList());

        return writeListValues(parsedValues);
    }

    @Nullable
    public String normalizeMapCriteria(@Nullable String value) {
        String trimmed = trimToNull(value);
        if (trimmed == null) {
            return null;
        }

        try {
            if (trimmed.startsWith("{")) {
                Map<String, Object> parsedValues = objectMapper.readValue(trimmed, new TypeReference<Map<String, Object>>() {});
                return writeMapValues(parsedValues);
            }
        } catch (JsonProcessingException ignored) {
            // Fall back to comma-separated key:value parsing for admin input.
        }

        Map<String, Integer> parsedValues = new LinkedHashMap<>();
        for (String entry : trimmed.split("[,\\n]")) {
            String normalizedEntry = trimToNull(entry);
            if (normalizedEntry == null) {
                continue;
            }

            String[] parts = normalizedEntry.split("[:=]", 2);
            if (parts.length != 2) {
                continue;
            }

            String key = trimToNull(parts[0]);
            String rawValue = trimToNull(parts[1]);
            if (key == null || rawValue == null) {
                continue;
            }

            try {
                parsedValues.put(key, Integer.parseInt(rawValue));
            } catch (NumberFormatException ignored) {
                // Skip invalid map values instead of storing malformed criteria.
            }
        }

        return writeMapValues(parsedValues);
    }

    @Nullable
    private String writeListValues(@Nullable List<String> values) {
        List<String> normalizedValues = values == null ? List.of() : values.stream()
                .map(this::trimToNull)
                .filter(item -> item != null)
                .distinct()
                .collect(Collectors.toList());

        if (normalizedValues.isEmpty()) {
            return null;
        }

        try {
            return objectMapper.writeValueAsString(normalizedValues);
        } catch (JsonProcessingException ex) {
            return null;
        }
    }

    @Nullable
    private String writeMapValues(@Nullable Map<String, ?> values) {
        if (values == null || values.isEmpty()) {
            return null;
        }

        Map<String, Object> normalizedValues = new LinkedHashMap<>();
        for (Map.Entry<String, ?> entry : values.entrySet()) {
            String key = trimToNull(entry.getKey());
            if (key == null || entry.getValue() == null) {
                continue;
            }
            normalizedValues.put(key, entry.getValue());
        }

        if (normalizedValues.isEmpty()) {
            return null;
        }

        try {
            return objectMapper.writeValueAsString(normalizedValues);
        } catch (JsonProcessingException ex) {
            return null;
        }
    }

    @Nullable
    private String trimToNull(@Nullable String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
