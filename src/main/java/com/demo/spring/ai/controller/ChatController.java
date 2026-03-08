package com.demo.spring.ai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ChatController {

    private final ChatClient chatClient;

    @Autowired
    public ChatController (ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @GetMapping("/chat/{message}")
    public String chat(@PathVariable String message) {
        return chatClient.prompt(message).call().content();
    }

}
