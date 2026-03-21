package com.demo.spring.ai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;


/**
 * This config will use the chat memory feature.
 * The memory will store either embedded memory or h2 DB
 */
@Configuration
public class ChatMemoryChatClientConfig {

    @Bean
    ChatMemory chatMemory(JdbcChatMemoryRepository jdbcChatMemoryRepository) {
        return MessageWindowChatMemory.builder().maxMessages(10) // H2 DB max stored messages amount per conversion id, each time fetch all
                .chatMemoryRepository(jdbcChatMemoryRepository).build();
    }

    /***
     * Using RetrievalAugmentationAdvisor embedded source code to fetch the similar messages
     *
     * 1. MessageChatMemoryAdvisor first will fetch all history user and assistant messages
     * 2. Append with the latest input user message and the default system message(I coded it before at the ChatClient)
     * 3. RetrievalAugmentationAdvisor r0w 146 wraps the fetched vector message and latest user message via a temple, and the whole is the user role message
     *
     * @param vectorStore
     * @return
     */
    @Bean
    RetrievalAugmentationAdvisor retrievalAugmentationAdvisor(VectorStore vectorStore) {
        return RetrievalAugmentationAdvisor.builder().documentRetriever(
                VectorStoreDocumentRetriever.
                        builder().
                        vectorStore(vectorStore).
                        topK(3).
                        similarityThreshold(0.5).
                        build()
                ).build();
    }

    /**
     * 1. DefaultAroundAdvisorChain - reorder for the assigned advisors via the priority, negative > 0 > positive
     * 2. MessageChatMemoryAdvisor(negative) -> SimpleLoggerAdvisor(0) -> ChatModelCallAdvisor(max positive) ->
     *  LLM -> ChatModelCallAdvisor -> SimpleLoggerAdvisor -> MessageChatMemoryAdvisor. Using recursive way at DefaultAroundAdvisorChain.
     * 3. ChatModelCallAdvisor is the essential part to add, I mean will auto added
     *
     * 4. I list the following workflow for the LLM memory call
     *    4.1) Client with the conversation_id to fetch the H2 for its history conversation(ONLY User and Assistant);
     *    4.2) Append this time http request's new user, system message with the 4.1 messages
     *    4.3) Save the new user role message at the before
     *    4.4) Call the LLM with the full messages at the 4.2 and get the assistant role response
     *    4.5) Save the assistant role message at the after - MessageChatMemoryAdvisor.java
     *
     * @param chatClientBuilder
     * @param chatMemory
     * @return
     */
    @Bean("chatMemoryChatClient")
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder, ChatMemory chatMemory, RetrievalAugmentationAdvisor retrievalAugmentationAdvisor) {

        ChatOptions chatOptions =
                        ChatOptions.builder().model(OpenAiApi.ChatModel.GPT_4_1_MINI.value). // choose a cheaper model
                        temperature(0.7). // Temperature controls the randomness or "creativity" of the model’s response
                        maxTokens(150). // The maxTokens parameter limits how many tokens (word pieces) the model can generate in its response.
                        build();

        Advisor loggerAdvisor = new SimpleLoggerAdvisor();
        Advisor memoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory).build();

        // generate a default DefaultChatClient instance which use the step builder pattern
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
                defaultAdvisors(List.of(loggerAdvisor, memoryAdvisor, retrievalAugmentationAdvisor)).
                build();
    }
}
