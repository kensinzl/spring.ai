package com.demo.spring.ai.config;

import com.demo.spring.ai.tool.HelpDeskTools;
import com.demo.spring.ai.tool.TimeTools;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Configuration
public class MCPServerConfig {

    @Bean
    public List<ToolCallback> getMCPServerTools(HelpDeskTools helpDeskTools, TimeTools timeTools) {
        // Explicitly use the MethodToolCallbackProvider
        return List.of(ToolCallbacks.from(helpDeskTools, timeTools));
    }
}
