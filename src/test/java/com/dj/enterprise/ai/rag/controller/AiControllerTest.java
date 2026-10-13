package com.dj.enterprise.ai.rag.controller;

import com.dj.enterprise.ai.rag.dto.AskRequest;
import com.dj.enterprise.ai.rag.dto.AskResponse;
import com.dj.enterprise.ai.rag.service.AiService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AiControllerTest {

    private AiService aiService;
    private AiController aiController;

    @BeforeEach
    void setUp() {
        aiService = mock(AiService.class);
        aiController = new AiController(aiService);
    }

    @Test
    void shouldReturnAnswerForQuestion() {
        String question =
                "How many days can employees work from home?";

        when(aiService.ask(question))
                .thenReturn("Two days per week.");

        AskResponse response =
                aiController.ask(new AskRequest(question));

        assertEquals("Two days per week.", response.answer());

        verify(aiService).ask(question);
    }
}