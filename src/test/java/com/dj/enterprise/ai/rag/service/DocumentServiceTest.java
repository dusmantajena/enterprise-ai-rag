package com.dj.enterprise.ai.rag.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

class DocumentServiceTest {

    private VectorStore vectorStore;
    private DocumentService documentService;

    @BeforeEach
    void setUp() {
        vectorStore = mock(VectorStore.class);
        documentService = new DocumentService(vectorStore);
    }

    @Test
    void shouldSplitAndStoreDocument() {
        String text = """
                The company allows employees to work from home
                two days per week. Annual leave entitlement is
                25 days excluding public holidays.
                """;

        int chunks = documentService.ingest(
                "company-policy.txt", text);

        assertTrue(chunks > 0);

        verify(vectorStore, times(1)).add(anyList());
    }

    @Test
    void shouldStoreDocumentWithSourceMetadata() {
        documentService.ingest(
                "company-policy.txt",
                "Employees can work from home two days per week.");

        @SuppressWarnings("unchecked")
        var captor = org.mockito.ArgumentCaptor.forClass(
                (Class<List<Document>>) (Class<?>) List.class);

        verify(vectorStore).add(captor.capture());

        List<Document> documents = captor.getValue();

        assertFalse(documents.isEmpty());

        assertEquals(
                "company-policy.txt",
                documents.getFirst().getMetadata().get("source"));
    }
}