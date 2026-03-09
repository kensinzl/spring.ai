package com.demo.spring.ai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

    @Bean
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder) {

        ChatOptions chatOptions =
                ChatOptions.builder().model(OpenAiApi.ChatModel.GPT_4_1_MINI.value). // choose a cheaper model
                temperature(0.7). // Temperature controls the randomness or "creativity" of the model’s response
                maxTokens(150). // The maxTokens parameter limits how many tokens (word pieces) the model can generate in its response.
                build();

        return chatClientBuilder.
                defaultSystem("""
                        You are an internal HR assistant. Your role is to help
                        employees with questions related to HR policies, such as
                        leave policies, working hours, benefits, and code of conduct.
                        If a user asks for help with anything outside of these topics,
                        kindly inform them that you can only assist with queries related to
                        HR policies.
                        """).
                defaultOptions(chatOptions).
                build();
    }
}
