package com.casy.casyaiagent.config;

import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepositoryDialect;

public class MyPostgresChatMemoryRepositoryDialect implements JdbcChatMemoryRepositoryDialect {
    public MyPostgresChatMemoryRepositoryDialect() {
    }

    public String getSelectMessagesSql() {
        return "SELECT content, type FROM T_SPRING_AI_CHAT_MEMORY WHERE chatId = ? ORDER BY \"timestamp\"";
    }

    public String getInsertMessageSql() {
        return "INSERT INTO T_SPRING_AI_CHAT_MEMORY (chatId, content, type, \"timestamp\") VALUES (?, ?, ?, ?)";
    }

    public String getSelectConversationIdsSql() {
        return "SELECT DISTINCT chatId FROM T_SPRING_AI_CHAT_MEMORY";
    }

    public String getDeleteMessagesSql() {
        return "DELETE FROM T_SPRING_AI_CHAT_MEMORY WHERE chatId = ?";
    }
}
