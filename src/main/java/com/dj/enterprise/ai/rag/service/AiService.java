package com.dj.enterprise.ai.rag.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

@Service
public class AiService {

    private final ChatClient chatClient;
    private final QuestionAnswerAdvisor ragAdvisor;

    public AiService(
            ChatClient.Builder chatClientBuilder,
            VectorStore vectorStore) {

        this.chatClient = chatClientBuilder.build();

        this.ragAdvisor = QuestionAnswerAdvisor.builder(vectorStore)
                .build();
    }

    public String ask(String question) {
        return chatClient.prompt()
                .system("""
                    You are a helpful enterprise assistant.
                    Answer using the retrieved document context.
                    If the context does not contain the answer,
                    say that the information was not found
                    in the supplied documents.
                    Do not invent facts.
                    """)
                .user(question)
                .advisors(ragAdvisor)
                .call()
                .content();
    }
}