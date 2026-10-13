package com.dj.enterprise.ai.rag.dto;

import jakarta.validation.constraints.NotBlank;

public record AskRequest(
        @NotBlank(message = "Question must not be empty")
        String question
) {
}