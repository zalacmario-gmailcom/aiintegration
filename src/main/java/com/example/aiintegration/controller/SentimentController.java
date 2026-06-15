package com.example.aiintegration.controller;

import com.example.aiintegration.client.AiClientService;
import com.example.aiintegration.dto.AiResponseDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SentimentController {

    private final AiClientService aiClientService;

    public SentimentController(AiClientService aiClientService) {
        this.aiClientService = aiClientService;
    }

    @GetMapping("/analyze")
    public AiResponseDto analyze(@RequestParam String text) {
        return aiClientService.analyzeSentiment(text);
    }
}
