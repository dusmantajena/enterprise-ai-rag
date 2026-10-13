package com.dj.enterprise.ai.rag.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record IngestRequest(
        @NotBlank(message = "Document text must not be empty")
        @Size(max = 100000, message = "Document is too large")
        String text,

        @NotBlank(message = "Document name must not be empty")
        String name
) {
}