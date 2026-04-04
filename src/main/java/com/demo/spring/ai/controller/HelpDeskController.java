package com.demo.spring.ai.controller;

import com.demo.spring.ai.tool.HelpDeskTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

@RestController
@RequestMapping("/api/tools")
public class HelpDeskController {

    private final ChatClient chatClient;
    private final HelpDeskTools helpDeskTools;
    private final Resource systemPromptTemplate;


    @Autowired
    public HelpDeskController(@Qualifier("chatMemoryChatClient") ChatClient chatClient,
                              HelpDeskTools helpDeskTools,
                              @Value("classpath:/promptTemplate/helpDeskSystemPromptTemplate.st") Resource systemPromptTemplate) {
        this.chatClient = chatClient;
        this.helpDeskTools = helpDeskTools;
        this.systemPromptTemplate = systemPromptTemplate;
    }

    @GetMapping("/help-desk")
    public ResponseEntity<String> helpDesk(@RequestHeader("username") String username,
                                           @RequestParam("message") String message) {
        String answer = chatClient.prompt()
                .system(this.systemPromptTemplate)
                .advisors(a -> a.param(CONVERSATION_ID, username))
                .user(message)
                .tools(helpDeskTools)
                .toolContext(Map.of("username", username))
                .call().content();
        return ResponseEntity.ok(answer);
    }
}
