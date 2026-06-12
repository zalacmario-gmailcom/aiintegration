package com.example.aiintegration.client;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class AiClientService {

    private static final Logger log = LoggerFactory.getLogger(AiClientService.class);

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

    public String fetchWithBackoff(String userInput) throws InterruptedException {
        int maxRetries = 3;
        long delay = 1000;

        for (int i = 0; i < maxRetries; i++) {
            ResponseEntity<String> response = restClient.post()
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
