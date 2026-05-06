package com.demo.spring.ai;

import com.demo.spring.ai.controller.ChatController;
import org.junit.jupiter.api.*;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.evaluation.FactCheckingEvaluator;
import org.springframework.ai.chat.evaluation.RelevancyEvaluator;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;


@SpringBootTest
@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(properties = {
        "spring.ai.openai.api-key = ${OPENAI_API_KEY:fakestring}"
})
public class MyTest {

    @Autowired
    private OpenAiChatModel openAiChatModel;

    @Autowired
    private ChatController chatController;

    private RelevancyEvaluator relevancyEvaluator;

    private FactCheckingEvaluator factCheckingEvaluator;

    @BeforeAll
    public void init() {

        /**
         * work around to make the identical to the production code
         */
        ChatOptions chatOptions =
                ChatOptions.builder().model(OpenAiApi.ChatModel.GPT_4_1_MINI.value). // choose a cheaper model
                        temperature(0.7). // Temperature controls the randomness or "creativity" of the model’s response
                        maxTokens(150). // The maxTokens parameter limits how many tokens (word pieces) the model can generate in its response(completion token).
                        build();

        ChatClient.Builder chatClientBuilder =
                ChatClient.builder(openAiChatModel).
                defaultSystem("""
                        You are an internal HR assistant. Your role is to help
                        employees with questions related to HR policies, such as
                        leave policies, working hours, benefits, and code of conduct.
                        If a user asks for help with anything outside of these topics,
                        kindly inform them that you can only assist with queries related to
                        HR policies.
                        """).
                defaultOptions(chatOptions).
                defaultAdvisors(List.of(new SimpleLoggerAdvisor()));

        relevancyEvaluator = new RelevancyEvaluator(chatClientBuilder);

        factCheckingEvaluator = FactCheckingEvaluator.forBespokeMinicheck(chatClientBuilder);
    }

    @Test
    @DisplayName("Should return relevant response for basic question")
    @Timeout(value = 30, unit = TimeUnit.SECONDS)
    public void checkRelevance() {
        //Given
        String question = "who are you?";

        // When
        String aiResponse = chatController.chat(question);

        // Then
        EvaluationRequest evaluationRequest = new EvaluationRequest(question, aiResponse);
        EvaluationResponse evaluationResponse = relevancyEvaluator.evaluate(evaluationRequest);

        assertAll(() -> assertThat(aiResponse).isNotBlank(),
                () -> assertThat(evaluationResponse.isPass()).withFailMessage(
                                """
                                ========================================
                                The answer was not considered relevant.
                                Question: "%s"
                                Response: "%s"
                                ========================================
                                """, question, aiResponse).isTrue(),
                () -> assertThat(evaluationResponse.getScore()).withFailMessage("""
                                ========================================
                                The score %.2f is lower than the minimum required %.2f.
                                Question: "%s"
                                Response: "%s"
                                ========================================
                                """, evaluationResponse.getScore(), 0.7F, question, aiResponse).isGreaterThanOrEqualTo(0.7F));

    }

//    @Test
//    public void checkFact() {
//        // Given
//        String question = "Who discovered the law of universal gravitation?";
//
//        // When
//        String aiResponse = chatController.chat(question);
//        EvaluationRequest evaluationRequest = new EvaluationRequest(question, aiResponse);
//        EvaluationResponse response = factCheckingEvaluator.evaluate(evaluationRequest);
//
//        assertAll(() -> assertThat(aiResponse).isNotBlank(),
//                () -> assertThat(response.isPass())
//                        .withFailMessage("""
//                             ========================================
//                             The answer was not considered factually correct.
//                             Question: "%s"
//                             Response: "%s"
//                             ========================================
//                                """, question, aiResponse)
//                        .isTrue());
//    }
}
