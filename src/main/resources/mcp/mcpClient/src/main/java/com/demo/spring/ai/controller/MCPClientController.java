package com.demo.spring.ai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class MCPClientController {

    private final ChatClient chatClient;

    @Autowired
    public MCPClientController(@Qualifier("simpleChatClient") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/mcp/chat")
    public String chat(@RequestHeader(value = "username",required = false) String username,
                       @RequestParam("message") String message) {
        return chatClient.prompt().user(message+ " My username is " + username)
                .call().content();
    }
}
