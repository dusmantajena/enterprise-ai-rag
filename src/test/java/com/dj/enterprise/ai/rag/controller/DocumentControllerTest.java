package com.dj.enterprise.ai.rag.controller;

import com.dj.enterprise.ai.rag.dto.IngestRequest;
import com.dj.enterprise.ai.rag.service.DocumentService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

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
    void shouldIndexTextDocument() {
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

        verify(documentService).ingest(name, text);
    }

    @Test
    void shouldUploadAndIndexPdf() throws Exception {
        MockMultipartFile pdf = new MockMultipartFile(
                "file",
                "company-policy.pdf",
                "application/pdf",
                "%PDF-test".getBytes()
        );

        when(documentService.ingestPdf(pdf)).thenReturn(3);

        ResponseEntity<Map<String, Object>> response =
                documentController.uploadPdf(pdf);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(
                "PDF indexed successfully",
                response.getBody().get("message"));
        assertEquals(
                "company-policy.pdf",
                response.getBody().get("fileName"));
        assertEquals(
                3,
                response.getBody().get("chunksCreated"));

        verify(documentService).ingestPdf(pdf);
    }

    @Test
    void shouldRejectEmptyPdf() {
        MockMultipartFile pdf = new MockMultipartFile(
                "file",
                "empty.pdf",
                "application/pdf",
                new byte[0]
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> documentController.uploadPdf(pdf));

        verifyNoInteractions(documentService);
    }
}