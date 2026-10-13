package com.dj.enterprise.ai.rag.service;

import java.util.List;
import java.util.Map;

import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

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
}