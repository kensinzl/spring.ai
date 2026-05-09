package com.demo.spring.ai.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.evaluation.RelevancyEvaluator;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("/api")
public class ChatController {

    private final ChatClient chatClient;
    private final Resource systemStuffingPrompt;
    private final Resource userPrompt;
    private final RelevancyEvaluator relevancyEvaluator;


    @Autowired
    public ChatController (@Qualifier("chatClient") ChatClient chatClient,
                           @Value("classpath:/promptTemplate/systemStuffingPrompt.st") Resource systemStuffingPrompt,
                           @Value("classpath:/promptTemplate/userPrompt.st") Resource userPrompt,
                           RelevancyEvaluator relevancyEvaluator) {
        this.chatClient = chatClient;
        this.systemStuffingPrompt = systemStuffingPrompt;
        this.userPrompt = userPrompt;
        this.relevancyEvaluator = relevancyEvaluator;
    }

    /**
     * - API level to override the default system message via the system role.
     * - Use the stuffing way, LLM can know the extra info
     * - Use the prompt template with param for the user role message
     *
     * @param message
     * @return the response from the LLM
     */
    @Retryable(retryFor = RuntimeException.class, maxAttemptsExpression = "${retry.maxAttempts}", backoff = @Backoff(delayExpression = "${retry.maxDelay}"))
    // @ConcurrencyLimit(5) -> SpringBoot4, good to avoid the multiple users for the concurrent invokes, https://www.baeldung.com/spring-retry#1-enabling-and-using-concurrencylimit
    @GetMapping("/chat/{message}")
    public String chat(@PathVariable("message") String message) {

        if(1==1) {
            // if the max is 3, it means the total trial amount is 3. it includes the init.
            log.info("Retry Number: "+ RetrySynchronizationManager.getContext().getRetryCount());
            log.info("throw RuntimeException in method retryService()");
            throw new RuntimeException("my intentional exception");
        }

        String aiResponse =
                chatClient.
                // spring ai source code wrap the str content as the user role
                //prompt(message).
                prompt().
                user(promptUserSpec -> {
                    promptUserSpec.text(userPrompt).param("customerName", "Liang").param("customerMessage", message);
                }).
                system(systemStuffingPrompt).
                call().content();
        EvaluationRequest evaluationRequest = new EvaluationRequest(message, aiResponse);
        EvaluationResponse response = relevancyEvaluator.evaluate(evaluationRequest);
        if(!response.isPass()) {
            throw new RuntimeException("not good response from LLM");
        }
        return aiResponse;
    }

    /**
     * 1. The recovery handler should have the first parameter of type Throwable (optional) and the same return type.
     * 2. The remaining arguments are populated from the argument list of the failed method in the same order.
     * 3. When the Retryable tried after three times, then still failed for the RuntimeException, it will trigger this recover
     *
     */
    @Recover
    public String chat(RuntimeException ex, String message) {
        log.info("After trying three times, now pointing to the recovery");
        // Use Java's String.format placeholders (%s). "{}" is used by logging frameworks (e.g., SLF4J),
        // so String.format won't replace them — that's why message and ex.getMessage() weren't printed.
        return String.format("I can not answer the request %s, because of the %s", message, ex.getMessage());
    }

}
