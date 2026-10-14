package com.dj.enterprise.ai.rag.controller;

import com.dj.enterprise.ai.rag.dto.IngestRequest;
import com.dj.enterprise.ai.rag.service.DocumentService;

import jakarta.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> ingest(
            @Valid @RequestBody IngestRequest request) {

        int chunks = documentService.ingest(
                request.name(), request.text());

        return ResponseEntity.ok(Map.of(
                "message", "Document indexed successfully",
                "documentName", request.name(),
                "chunksCreated", chunks
        ));
    }

    @PostMapping(
            value = "/pdf",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<Map<String, Object>> uploadPdf(
            @RequestParam("file") MultipartFile file)
            throws IOException {

        if (file.isEmpty()
                || !"application/pdf".equalsIgnoreCase(
                file.getContentType())) {
            throw new IllegalArgumentException(
                    "Please upload a PDF file");
        }

        int chunks = documentService.ingestPdf(file);

        return ResponseEntity.ok(Map.of(
                "message", "PDF indexed successfully",
                "fileName", file.getOriginalFilename(),
                "chunksCreated", chunks
        ));
    }
}