package com.casy.casyaiagent.config;

import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepositoryDialect;
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
        return new CustomMysqlDialect("my_custom_chat_memory");
    }

    @Bean
    public JdbcChatMemoryRepository chatMemoryRepository(JdbcTemplate jdbcTemplate,
                                                         JdbcChatMemoryRepositoryDialect customDialect) {
        // 可选：在Repository创建时执行建表语句（仅推荐在开发环境或初始化时使用）
         jdbcTemplate.execute(((CustomMysqlDialect)customDialect).getCreateTableSql());
         jdbcTemplate.execute(((CustomMysqlDialect)customDialect).getCreateIndexSql());

        return JdbcChatMemoryRepository.builder()
                .jdbcTemplate(jdbcTemplate)
                .dialect(customDialect) // 注入自定义的Dialect
                .build();
    }
}