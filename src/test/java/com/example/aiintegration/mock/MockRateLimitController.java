package com.example.aiintegration.mock;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Steg 6 - Kantfall 2: Simulerar en AI-tjänst som alltid svarar med 429 Too
 * Many Requests.
 * Aktiveras enbart under test-profilen "rate-limit-test".
 * Rikta din klient mot http://localhost:{port}/v1/chat/completions för att se
 * backoff-loopen.
 */
@Profile("rate-limit-test")
@RestController
@RequestMapping("/v1/chat/completions")
public class MockRateLimitController {

    @PostMapping
    public ResponseEntity<String> alwaysRateLimit() {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body("{\"error\": {\"message\": \"Rate limit exceeded\", \"type\": \"requests\"}}");
    }
}
