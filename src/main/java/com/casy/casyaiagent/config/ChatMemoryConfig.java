package com.casy.casyaiagent.config;

import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.memory.repository.jdbc.MysqlChatMemoryRepositoryDialect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class ChatMemoryConfig {

    @Bean
    public ChatMemory chatMysqlMemory(JdbcTemplate jdbcTemplate) {
        // 复用您的代码逻辑来构建Bean
        JdbcChatMemoryRepository repository = JdbcChatMemoryRepository.builder()
                .jdbcTemplate(jdbcTemplate)
                .dialect(new MysqlChatMemoryRepositoryDialect())
                .dialect(new CustomTableNameDialect()) // 传入您的表名
                .build();
                
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(repository)
                .maxMessages(10) // 设置记忆窗口大小
                .build();
    }
}