package com.demo.spring.ai.tool;

import lombok.extern.log4j.Log4j2;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.ZoneId;


/**
 * Tool Calling
 *  1. Tools info + User Message and etc are sent to the LLM
 *  2. LLM decides which tool to use(null or one or more) if needs, eg: TOOL_CALL
 *  3. Tool Call Result(STOP) + User + System + Original Assistant message will be sent to the LLM AGAIN
 *  4. LLM gives the result
 *
 *  OpenAiChatModel(internalCall) -> DefaultToolCallingManager(executeToolCalls) invoke the Tool function
 *
 *  internalCall function - you can find the itself invokes twice depends on the returnDirect boolean, one is step 1 and the other is step 3
 *
 */
@Component
@Log4j2
public class TimeTools {

    @Tool(name="getCurrentLocalTime", description = "Get the current time in the user's timezone")
    String getCurrentLocalTime() {
        log.info("Returning the current time in the user's timezone");
        return LocalTime.now().toString();
    }

    @Tool(name = "getCurrentTime", description = "Get the current time in the specified time zone.")
    public String getCurrentTime(
            @ToolParam(description = "Value representing the time zone") String timeZone) {
        log.info("Returning the current time in the timezone {}", timeZone);
        return LocalTime.now(ZoneId.of(timeZone)).toString();
    }
}
