package com.example.aiintegration.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record AiResponseDto(

        @NotNull @Pattern(regexp = "^(positive|negative|neutral)$", message = "Sentiment must be positive, negative, or neutral") String sentiment,

        @DecimalMin(value = "0.0", message = "Confidence must be at least 0.0") @DecimalMax(value = "1.0", message = "Confidence must be at most 1.0") double confidence) {
}
