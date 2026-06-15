package com.example.aiintegration.client;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class AiClientService {

    private static final Logger log = LoggerFactory.getLogger(AiClientService.class);

    // (1) Systemprompt — låser AI:ns beteende, tvingar fram rent JSON utan markdown
    private static final String SYSTEM_PROMPT = """
            You are a sentiment analysis engine.
            Analyze the sentiment of the user's text and respond ONLY with a JSON object.
            The JSON must follow this exact schema: {"sentiment": "positive"|"negative"|"neutral", "confidence": <0.0-1.0>}
            Do NOT include markdown formatting, code blocks, or any conversational text.
            """;

    @Value("${openai.api.key}")
    private String apiKey;

    private RestClient restClient;

    @PostConstruct
    public void init() {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("CRITICAL: API key is missing.");
        }

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(2000);
        factory.setReadTimeout(8000);

        this.restClient = RestClient.builder()
                .requestFactory(factory)
                .build();
    }

    public String analyzeSentiment(String userInput) throws InterruptedException {
        // (2) Bygg payload — system och user är SEPARATA fält (skydd mot prompt
        // injection)
        Map<String, Object> payload = Map.of(
                "model", "gpt-4o-mini",
                "temperature", 0.1, // (3) Låg temperature = deterministiskt svar
                "messages", List.of(
                        Map.of("role", "system", "content", SYSTEM_PROMPT), // Fast instruktion
                        Map.of("role", "user", "content", userInput) // Dynamisk indata
                ));

        return fetchWithBackoff(payload);
    }

    private String fetchWithBackoff(Map<String, Object> payload) throws InterruptedException {
        int maxRetries = 3;
        long delay = 1000;

        for (int i = 0; i < maxRetries; i++) {
            ResponseEntity<String> response = restClient.post()
                    .uri("https://api.openai.com/v1/chat/completions")
                    .header("Authorization", "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .toEntity(String.class);

            if (response.getStatusCode().value() == 429) {
                log.warn("Rate limit hit (429). Retrying in {} ms... (attempt {}/{})", delay, i + 1, maxRetries);
                Thread.sleep(delay);
                delay *= 2;
                continue;
            }

            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }

            log.error("Request failed with status code {}: {}", response.getStatusCode(), response.getBody());
            throw new RuntimeException("Request failed with status code " + response.getStatusCode());
        }
        throw new RuntimeException("Max retries exceeded due to rate limiting.");
    }
}
