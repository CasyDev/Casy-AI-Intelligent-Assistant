package com.casy.casyaiagent.config;

import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepositoryDialect;
import org.springframework.ai.chat.memory.repository.jdbc.MysqlChatMemoryRepositoryDialect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * JDBC 对话记忆配置类
 * 仅在 loveapp.chat.memory.type=jdbc 时生效
 */
@Configuration
@ConditionalOnProperty(name = "loveapp.chat.memory.type", havingValue = "jdbc")
public class ChatMemoryConfig {

    @Bean
    public JdbcChatMemoryRepositoryDialect jdbcChatMemoryRepositoryDialect() {
        // 根据需要可以切换为 PostgreSQL 或其他数据库方言
        return new MysqlChatMemoryRepositoryDialect();
    }

    @Bean
    public JdbcChatMemoryRepository chatMemoryRepository(JdbcTemplate jdbcTemplate,
                                                         JdbcChatMemoryRepositoryDialect jdbcChatMemoryRepositoryDialect) {
        return JdbcChatMemoryRepository.builder()
                .jdbcTemplate(jdbcTemplate)
                .dialect(jdbcChatMemoryRepositoryDialect)
                .build();
    }
}
