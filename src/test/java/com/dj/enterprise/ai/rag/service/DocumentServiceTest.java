package com.dj.enterprise.ai.rag.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedConstruction;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.vectorstore.VectorStore;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
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
    void shouldSplitAndStoreTextDocument() {
        int chunks = documentService.ingest(
                "company-policy.txt",
                "Employees can work from home two days per week.");

        assertTrue(chunks > 0);
        verify(vectorStore).add(anyList());
    }

    @Test
    void shouldReadSplitAndStorePdf() throws Exception {
        List<Document> pages = List.of(
                new Document(
                        "Employees can work from home two days per week.",
                        Map.of("page_number", 1)
                )
        );

        try (MockedConstruction<PagePdfDocumentReader> mockedReader =
                     mockConstruction(
                             PagePdfDocumentReader.class,
                             (reader, context) ->
                                     when(reader.get()).thenReturn(pages))) {

            org.springframework.mock.web.MockMultipartFile pdf =
                    new org.springframework.mock.web.MockMultipartFile(
                            "file",
                            "company-policy.pdf",
                            "application/pdf",
                            "%PDF-test".getBytes()
                    );

            int chunks = documentService.ingestPdf(pdf);

            assertTrue(chunks > 0);
            assertEquals(1, mockedReader.constructed().size());

            @SuppressWarnings("unchecked")
            ArgumentCaptor<List<Document>> captor =
                    ArgumentCaptor.forClass(List.class);

            verify(vectorStore).add(captor.capture());

            List<Document> storedDocuments = captor.getValue();

            assertFalse(storedDocuments.isEmpty());
            assertEquals(
                    "company-policy.pdf",
                    storedDocuments.getFirst()
                            .getMetadata().get("source")
            );
        }
    }
}