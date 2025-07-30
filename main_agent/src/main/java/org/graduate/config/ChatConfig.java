package org.graduate.config;

import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.deepseek.DeepseekChatModel;
import org.springframework.ai.chat.client.DefaultChatClient;
import org.springframework.ai.tool.DefaultToolService;
import org.springframework.ai.tool.ToolService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatConfig {

    @Value("${spring.ai.deepseek.api-key}")
    private String apiKey;

    @Value("${spring.ai.deepseek.base-url}")
    private String baseUrl;

    @Bean
    public ChatModel chatModel() {
        return DeepseekChatModel.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .build();
    }

    @Bean
    public ChatClient chatClient(ChatModel chatModel) {
        return new DefaultChatClient(chatModel);
    }

    @Bean
    public ToolService toolService(ChatClient chatClient) {
        return new DefaultToolService(chatClient);
    }
} 