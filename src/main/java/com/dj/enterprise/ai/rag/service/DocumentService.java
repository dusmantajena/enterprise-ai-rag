package com.dj.enterprise.ai.rag.service;

import java.util.List;
import java.util.Map;

import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DocumentService {

    private final VectorStore vectorStore;

    private final TokenTextSplitter textSplitter =
            TokenTextSplitter.builder().build();

    public DocumentService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public int ingest(String name, String text) {
        Document document = new Document(
                text,
                Map.of("source", name)
        );

        List<Document> chunks = textSplitter.split(document);

        vectorStore.add(chunks);

        return chunks.size();
    }

    public int ingestPdf(MultipartFile file) throws IOException {

        Path tempFile = Files.createTempFile("rag-", ".pdf");

        try {
            file.transferTo(tempFile);

            PagePdfDocumentReader reader =
                    new PagePdfDocumentReader(
                            new FileSystemResource(tempFile));

            List<Document> pages = reader.get();

            List<Document> documents = pages.stream()
                    .map(page -> new Document(
                            page.getText(),
                            Map.of(
                                    "source",
                                    file.getOriginalFilename(),
                                    "page",
                                    page.getMetadata().getOrDefault(
                                            "page_number", "unknown")
                            )
                    ))
                    .toList();

            List<Document> chunks = textSplitter.split(documents);

            vectorStore.add(chunks);

            return chunks.size();

        } finally {
            Files.deleteIfExists(tempFile);
        }
    }
}