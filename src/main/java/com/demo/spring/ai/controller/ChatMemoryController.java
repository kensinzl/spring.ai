package com.demo.spring.ai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

@RestController
@RequestMapping("/api")
public class ChatMemoryController {

    private final ChatClient chatMemoryChatClient;
    private final Resource systemStuffingPrompt;


    @Autowired
    public ChatMemoryController(@Qualifier("chatMemoryChatClient") ChatClient chatMemoryChatClient,
                                @Value("classpath:/promptTemplate/systemStuffingPrompt.st") Resource systemStuffingPrompt) {
        this.chatMemoryChatClient = chatMemoryChatClient;
        this.systemStuffingPrompt = systemStuffingPrompt;
    }


    /**
     * Intentional way to split the step by step to debug the source code
     *
     * @param username
     * @param message
     * @return
     */
    @GetMapping("/chat-memory")
    public ResponseEntity<String> chatMemory(@RequestHeader("username") String username, @RequestParam("message") String message) {

        ChatClient.ChatClientRequestSpec defaultChatClientRequestSpec = chatMemoryChatClient.prompt();
        defaultChatClientRequestSpec = defaultChatClientRequestSpec.user(message); // DefaultChatClientUtils will wrap it into related role message list
        defaultChatClientRequestSpec = defaultChatClientRequestSpec.system(systemStuffingPrompt);
        defaultChatClientRequestSpec = defaultChatClientRequestSpec.advisors(advisorSpec -> advisorSpec.param(CONVERSATION_ID, username));
        ChatClient.CallResponseSpec callResponseSpec = defaultChatClientRequestSpec.call();
        String response = callResponseSpec.content();
        return ResponseEntity.ok(response);



//        return ResponseEntity.ok(chatMemoryChatClient.
//                                    prompt().
//                                    user(message).
//                                    advisors(advisorSpec -> advisorSpec.param(CONVERSATION_ID, username)).
//                                    call().content()
//        );
    }
}
