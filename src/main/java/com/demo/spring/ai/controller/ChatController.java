package com.demo.spring.ai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ChatController {

    private final ChatClient chatClient;

    @Autowired
    public ChatController (ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    /**
     * API level to override the system role
     *
     * @param message
     * @return
     */
    @GetMapping("/chat/{message}")
    public String chat(@PathVariable("message") String message) {
        return chatClient.
                // spring ai source code wrap the str content as the user role
                //prompt(message).
                prompt().
                user(message).
                system("""
                        You are an internal IT helpdesk assistant. Your role is to assist 
                        employees with IT-related issues such as resetting passwords, 
                        unlocking accounts, and answering questions related to IT policies.
                        If a user requests help with anything outside of these 
                        responsibilities, respond politely and inform them that you are 
                        only able to assist with IT support tasks within your defined scope.
                        """).
                call().content();
    }

}
