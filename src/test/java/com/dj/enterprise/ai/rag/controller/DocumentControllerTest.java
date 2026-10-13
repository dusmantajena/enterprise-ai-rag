package com.dj.enterprise.ai.rag.controller;

import com.dj.enterprise.ai.rag.dto.IngestRequest;
import com.dj.enterprise.ai.rag.service.DocumentService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DocumentControllerTest {

    private DocumentService documentService;
    private DocumentController documentController;

    @BeforeEach
    void setUp() {
        documentService = mock(DocumentService.class);
        documentController =
                new DocumentController(documentService);
    }

    @Test
    void shouldReturnSuccessAfterIndexingDocument() {
        String name = "company-policy.txt";
        String text = "Employees can work from home two days per week.";

        when(documentService.ingest(name, text)).thenReturn(1);

        ResponseEntity<Map<String, Object>> response =
                documentController.ingest(
                        new IngestRequest(text, name));

        assertEquals(200, response.getStatusCode().value());
        assertEquals(
                "Document indexed successfully",
                response.getBody().get("message"));
        assertEquals(
                name,
                response.getBody().get("documentName"));
        assertEquals(
                1,
                response.getBody().get("chunksCreated"));

        verify(documentService).ingest(name, text);
    }
}