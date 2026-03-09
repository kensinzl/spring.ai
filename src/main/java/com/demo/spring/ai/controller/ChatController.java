package com.demo.spring.ai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.Resource;

@RestController
@RequestMapping("/api")
public class ChatController {

    private final ChatClient chatClient;

    private final Resource systemStuffingPrompt;
    private final Resource userPrompt;


    @Autowired
    public ChatController (ChatClient chatClient,
                           @Value("classpath:/promptTemplate/systemStuffingPrompt.st") Resource systemStuffingPrompt,
                           @Value("classpath:/promptTemplate/userPrompt.st") Resource userPrompt) {
        this.chatClient = chatClient;
        this.systemStuffingPrompt = systemStuffingPrompt;
        this.userPrompt = userPrompt;
    }

    /**
     * - API level to override the default system message via the system role.
     * - Use the stuffing way, LLM can know the extra info
     * - Use the prompt template with param for the user role message
     *
     * @param message
     * @return the response from the LLM
     */
    @GetMapping("/chat/{message}")
    public String chat(@PathVariable("message") String message) {
        ChatResponse chatResponse =
                chatClient.
                // spring ai source code wrap the str content as the user role
                //prompt(message).
                prompt().
                user(promptUserSpec -> {
                    promptUserSpec.text(userPrompt).param("customerName", "Liang").param("customerMessage", message);
                }).
                system(systemStuffingPrompt).
                call().chatResponse();

        System.out.println("--- Model: " + chatResponse.getMetadata().getModel());
        return chatResponse.getResult().getOutput().getText();
    }
}
