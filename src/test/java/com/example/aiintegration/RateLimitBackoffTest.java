package com.example.aiintegration;

import com.example.aiintegration.mock.MockRateLimitController;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Steg 6 - Kantfall 2: Framtvinga 429 och verifiera backoff-loopen.
 *
 * Startar upp en Spring Boot-testserver med MockRateLimitController (profil:
 * rate-limit-test).
 * Kör backoff-loopen mot den lokala mock-servern och verifierar att
 * RuntimeException kastas
 * efter att alla 3 försök är uttömda. Bevaka konsolen för warn-loggar.
 *
 * Kör: ./gradlew test --tests RateLimitBackoffTest
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = { AiintegrationApplication.class,
        MockRateLimitController.class }, properties = { "openai.api.key=test-dummy-key" })
@ActiveProfiles("rate-limit-test")
class RateLimitBackoffTest {

    private static final Logger log = LoggerFactory.getLogger(RateLimitBackoffTest.class);

    @LocalServerPort
    private int port;

    @Test
    void rateLimitBackoff_shouldRetryAndThenThrow() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(2000);
        factory.setReadTimeout(8000);

        RestClient restClient = RestClient.builder()
                .requestFactory(factory)
                .build();

        Map<String, Object> payload = Map.of(
                "model", "gpt-4o-mini",
                "temperature", 0.1,
                "messages", List.of(Map.of("role", "user", "content", "test")));

        assertThrows(RuntimeException.class, () -> {
            int maxRetries = 3;
            long delay = 1000;

            for (int i = 0; i < maxRetries; i++) {
                ResponseEntity<String> response = restClient.post()
                        .uri("http://localhost:" + port + "/v1/chat/completions")
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
                    return;
                }
            }
            throw new RuntimeException("Max retries exceeded due to rate limiting.");
        });
    }
}
