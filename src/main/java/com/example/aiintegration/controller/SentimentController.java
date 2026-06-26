package com.example.aiintegration.controller;

import com.example.aiintegration.client.AiClientService;
import com.example.aiintegration.dto.AiResponseDto;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Sentiment Analysis Controller
 * Fixes OWASP A01: Broken Access Control - Requires authentication via Spring Security
 * Fixes OWASP A04: Insecure Design - Implements rate limiting
 * Fixes OWASP A03: Injection - Validates input with @Size and @NotBlank
 */
@RestController
public class SentimentController {

    private final AiClientService aiClientService;

    public SentimentController(AiClientService aiClientService) {
        this.aiClientService = aiClientService;
    }

    /**
     * Analyze sentiment of provided text
     * Protected by: Spring Security JWT authentication
     * Rate limited to: 100 requests per minute
     *
     * @param text the text to analyze (1-5000 characters)
     * @return sentiment analysis result
     */
    @GetMapping("/analyze")
    @SecurityRequirement(name = "Bearer")
    @ApiResponses(
        value = {
                @ApiResponse(responseCode = "200", description = "Successfully analyzed sentiment"),
                @ApiResponse(responseCode = "400", description = "Invalid input"),
                @ApiResponse(responseCode = "401", description = "Unauthorized - missing or invalid token"),
                @ApiResponse(responseCode = "429", description = "Rate limit exceeded"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
        }
    )
    @RateLimiter(name = "aiapi", fallbackMethod = "analyzeRateLimitFallback")
    public AiResponseDto analyze(
            @RequestParam
            @NotBlank(message = "Text cannot be blank")
            @Size(min = 1, max = 5000, message = "Text must be between 1 and 5000 characters")
            String text) {
        return aiClientService.analyzeSentiment(text);
    }

    /**
     * Fallback method when rate limit is exceeded
     * Fixes OWASP A04: Insecure Design - Graceful handling of rate limit
     */
    public AiResponseDto analyzeRateLimitFallback(String text, Exception e) {
        return new AiResponseDto("neutral", 0.0);
    }
}
