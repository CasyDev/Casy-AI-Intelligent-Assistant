package com.casy.casyaiagent.config;

import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepositoryDialect;
import org.springframework.ai.chat.memory.repository.jdbc.MysqlChatMemoryRepositoryDialect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author Administrator
 */
@Configuration
public class ChatMemoryConfig {
    @Bean
    public JdbcChatMemoryRepositoryDialect customDialect() {
        // 将 "my_custom_chat_memory" 替换为您想要的表名
        //return new CustomMysqlChatMemoryDialect("my_custom_chat_memory");
        return new MysqlChatMemoryRepositoryDialect();
    }

    @Bean
    public JdbcChatMemoryRepository chatMemoryRepository(JdbcTemplate jdbcTemplate,
                                                         JdbcChatMemoryRepositoryDialect customDialect) {
        return JdbcChatMemoryRepository.builder()
                .jdbcTemplate(jdbcTemplate)
                .dialect(customDialect) // 注入自定义的Dialect
                .build();
    }
}