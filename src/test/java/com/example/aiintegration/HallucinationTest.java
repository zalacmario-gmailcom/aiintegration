package com.example.aiintegration;

import com.example.aiintegration.dto.AiResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Steg 6 - Kantfall 3: Framtvinga Hallucination
 *
 * Ersätter det faktiska HTTP-svaret med en trasig sträng och verifierar
 * att catch-blocket fångar JsonProcessingException och returnerar fallback.
 *
 * Kör: ./gradlew test --tests HallucinationTest
 */
class HallucinationTest {

    private static final Logger log = LoggerFactory.getLogger(HallucinationTest.class);
    private static final AiResponseDto FALLBACK = new AiResponseDto("neutral", 0.0);

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Simulerar parselogiken i AiClientService.analyzeSentiment()
     * med ett trasigt, icke-JSON-svar från AI:n.
     */
    private AiResponseDto parseWithFallback(String rawAiContent) {
        try {
            AiResponseDto response = objectMapper.readValue(rawAiContent, AiResponseDto.class);
            return switch (response.sentiment()) {
                case "positive", "negative", "neutral" -> response;
                default -> {
                    log.warn("Unexpected sentiment '{}'. Returning fallback.", response.sentiment());
                    yield FALLBACK;
                }
            };
        } catch (JsonProcessingException e) {
            log.error("Failed to parse AI response. Returning fallback. Error: {}", e.getMessage());
            return FALLBACK;
        }
    }

    @Test
    void hallucinatedResponse_shouldReturnFallback() {
        // Simulerar ett konversationssvar istället för JSON
        String hallucination = "Sure! Here's your summary: the text is very positive and happy!";

        AiResponseDto result = parseWithFallback(hallucination);

        assertEquals("neutral", result.sentiment());
        assertEquals(0.0, result.confidence());
    }

    @Test
    void unexpectedSentimentValue_shouldReturnFallback() {
        // Giltig JSON men ogiltigt sentimentvärde
        String invalidSentiment = "{\"sentiment\": \"ecstatic\", \"confidence\": 0.99}";

        AiResponseDto result = parseWithFallback(invalidSentiment);

        assertEquals("neutral", result.sentiment());
        assertEquals(0.0, result.confidence());
    }

    @Test
    void validResponse_shouldReturnParsedDto() {
        String validJson = "{\"sentiment\": \"positive\", \"confidence\": 0.95}";

        AiResponseDto result = parseWithFallback(validJson);

        assertEquals("positive", result.sentiment());
        assertEquals(0.95, result.confidence());
    }
}
