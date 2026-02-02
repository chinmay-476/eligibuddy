package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class GeminiApiTest {
    
    @Test
    public void testGeminiApiKey() {
        String apiKey = System.getenv("GEMINI_API_KEY");
        if (apiKey == null || apiKey.trim().isEmpty()) {
            System.out.println("❌ GEMINI_API_KEY environment variable is not set!");
            System.out.println("Please set your API key:");
            System.out.println("Windows: set GEMINI_API_KEY=your_api_key_here");
            System.out.println("PowerShell: $env:GEMINI_API_KEY=\"your_api_key_here\"");
        } else {
            System.out.println("✅ GEMINI_API_KEY is configured: " + apiKey.substring(0, 10) + "...");
        }
    }
}
