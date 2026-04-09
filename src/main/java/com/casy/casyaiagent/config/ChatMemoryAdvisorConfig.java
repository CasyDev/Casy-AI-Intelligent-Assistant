package com.casy.casyaiagent.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * LoveApp 对话记忆配置类
 * 支持通过配置切换内存存储和数据库存储
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(LoveAppChatMemoryProperties.class)
public class ChatMemoryAdvisorConfig {

    /**
     * 基于内存的对话记忆（默认）
     */
    @Bean
    @ConditionalOnProperty(name = "loveapp.chat.memory.type", havingValue = "in-memory", matchIfMissing = true)
    public ChatMemory inMemoryChatMemory(LoveAppChatMemoryProperties properties) {
        log.info("使用基于内存的对话记忆，最大消息数: {}", properties.getMaxMessages());
        // MessageWindowChatMemory 不设置 repository 时，默认使用内存存储
        return MessageWindowChatMemory.builder()
                .maxMessages(properties.getMaxMessages())
                .build();
    }

    /**
     * 基于数据库的对话记忆
     */
    @Bean
    @ConditionalOnProperty(name = "loveapp.chat.memory.type", havingValue = "jdbc")
    public ChatMemory jdbcChatMemory(JdbcChatMemoryRepository chatMemoryRepository, 
                                     LoveAppChatMemoryProperties properties) {
        log.info("使用基于数据库的对话记忆，最大消息数: {}", properties.getMaxMessages());
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(chatMemoryRepository)
                .maxMessages(properties.getMaxMessages())
                .build();
    }

    /**
     * 对话记忆 Advisor
     */
    @Bean
    public MessageChatMemoryAdvisor chatMemoryAdvisor(ChatMemory inMemoryChatMemory) {
        return MessageChatMemoryAdvisor.builder(inMemoryChatMemory).build();
    }
}
