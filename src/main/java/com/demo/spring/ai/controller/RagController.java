package com.demo.spring.ai.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;


@RestController
@RequestMapping("/api")
@Log4j2
public class RagController {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final Resource promptTemplate;

    @Autowired
    public RagController (VectorStore vectorStore,
                          @Qualifier("chatMemoryChatClient") ChatClient chatClient,
                          @Value("classpath:/promptTemplate/systemPromptRandomDataTemplate.st") Resource promptTemplate) {
        this.chatClient = chatClient;
        this.vectorStore = vectorStore;
        this.promptTemplate = promptTemplate;
    }

    /**
     * 1. Take the input message content into Qdrant to find the most similar three sentences and will append them into system role message
     * 2. Each conversation id's history context(max 10 user and assistant role message) and the latest user message will be auto append as the context to AI
     *
     *
     * @param username
     * @param message
     * @return
     */
    @GetMapping("/embedded")
    public ResponseEntity<String> chatMemory(@RequestHeader("username") String username, @RequestParam("message") String message) {

        SearchRequest ragSearchRequest = SearchRequest.builder().query(message).topK(3).similarityThreshold(0.5).build();
        List<Document> similarDocs =  vectorStore.similaritySearch(ragSearchRequest);
        log.info("Fetch {} Similar Docs From Qdrant. ",  similarDocs.size());

        String similarContext =
                similarDocs.stream().map(Document::getText).collect(Collectors.joining(System.lineSeparator()));
        log.info("Similar Context will be appended into system role. \n {}",  similarContext);

        String answer =
                chatClient.
                prompt().
                system(promptSystemSpec -> promptSystemSpec.text(promptTemplate).param("documents", similarContext)).
                advisors(advisorSpec -> advisorSpec.param(CONVERSATION_ID, username)).
                user(message).
                call().content();
        return ResponseEntity.ok(answer);
    }
}
