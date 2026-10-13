package com.dj.enterprise.ai.rag.controller;

import com.dj.enterprise.ai.rag.dto.AskRequest;
import com.dj.enterprise.ai.rag.dto.AskResponse;
import com.dj.enterprise.ai.rag.service.AiService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ai")
public class AiController {

    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/ask")
    public AskResponse ask(
            @Valid @RequestBody AskRequest request) {

        return new AskResponse(
                aiService.ask(request.question()));
    }
}