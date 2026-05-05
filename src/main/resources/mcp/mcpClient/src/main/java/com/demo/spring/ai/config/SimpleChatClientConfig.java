package com.demo.spring.ai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SimpleChatClientConfig {

    /**
     * The mcp servers will use the npx to download the node package into local, after shutdown, then delete
     * ps aux | grep server-filesystem
     *
     * {@link io.modelcontextprotocol.client.McpSyncClient}
     *
     *
     *
     * tool/list ->
     *      ChatClient.call()(Hit API) ->
     *      DefaultChatClientUtils::toChatClientRequest R132 ->
     *      ToolCallbackProvider {@link org.springframework.ai.mcp.SyncMcpToolCallbackProvider::getToolCallbacks}
     *  DefaultChatClient set ToolCallbackProvider(defaultToolCallbacks(toolCallbackProvider)) and then getToolCallbackProviders is invoked by DefaultChatClientUtils::toChatClientRequest
     *
     * tool/call ->
     *      ChatModelCallAdvisor::adviseCall ->
     *      OpenAiChatModel::internalCall ->
     *      DefaultToolCallingManager::executeToolCalls -> executeToolCall ->
     *      SyncMcpToolCallback::call ->
     *      McpSyncClient::callTool
     *
     *  rurounikenshin-2:~ zhaoliang$ ps aux | grep server-filesystem
     * zhaoliang        28680   0.0  0.0 34122844    872 s000  S+    3:16pm   0:00.00 grep server-filesystem
     * zhaoliang        28665   0.0  0.3 45390364  54252   ??  S     3:15pm   0:00.53 node /Users/zhaoliang/.npm/_npx/a3241bba59c344f5/node_modules/.bin/mcp-server-filesystem /Users/zhaoliang/Workspace/spring.ai
     * zhaoliang        28654   0.0  0.4 34936128  68452   ??  S     3:15pm   0:01.28 npm exec @modelcontextprotocol/server-filesystem /Users/zhaoliang/Workspace/spring.ai
     */
    @Bean("simpleChatClient")
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder, ToolCallbackProvider toolCallbackProvider) {
        return chatClientBuilder.
                defaultToolCallbacks(toolCallbackProvider).
                defaultAdvisors(new SimpleLoggerAdvisor()).
                build();
    }
}
