package com.casy.casyaiagent.rag;

import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepositoryDialect;
import org.jetbrains.annotations.NotNull;

/**
 * 自定义Dialect，包含新增字段的SQL操作
 * @author linlin
 * @deprecated 已弃用，自定义的持久化聊天记录表，只能自定义字段名无法新增字段，因此弃用，不如直接用SpringAi自带的
 */
@Deprecated
public class CustomMysqlChatMemoryDialect implements JdbcChatMemoryRepositoryDialect {

    private final String tableName;

    public CustomMysqlChatMemoryDialect(String myCustomChatMemory) {
        this.tableName = myCustomChatMemory;
    }

    @NotNull
    @Override
    public String getSelectMessagesSql() {
        // 查询时包含自定义字段：role、message_order
        return String.format("SELECT content, type, role, message_order FROM %s WHERE conversation_id = ? ORDER BY `timestamp`", tableName);
    }

    @NotNull
    @Override
    public String getInsertMessageSql() {
        // 插入时包含自定义字段：role、message_order（占位符数量要和字段数一致）
        return String.format("INSERT INTO %s (conversation_id, content, type, `timestamp`, role, message_order) VALUES (?, ?, ?, ?, ?, ?)", tableName);
    }

    @NotNull
    @Override
    public String getSelectConversationIdsSql() {
        // 保留内置逻辑，无需改
        return String.format("SELECT DISTINCT conversation_id FROM %s", tableName);
    }

    @NotNull
    @Override
    public String getDeleteMessagesSql() {
        // 保留内置逻辑，无需改
        return String.format("DELETE FROM %s WHERE conversation_id = ?", tableName);
    }

    public String getCreateTableSql() {
        return String.format("""
                CREATE TABLE IF NOT EXISTS %s (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    conversation_id VARCHAR(255) NOT NULL,
                    content TEXT NOT NULL,
                    type VARCHAR(50) NOT NULL,  -- 内置字段（保留）
                    `timestamp` DATETIME NOT NULL,  -- 内置字段（保留）
                    role VARCHAR(50) NOT NULL,  -- 自定义字段：消息角色（user/assistant/system）
                    message_order INT NOT NULL,  -- 自定义字段：会话内消息顺序（1、2、3...）
                    INDEX idx_spring_ai_chat_memory_cid (conversation_id)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
                """, tableName);
    }
}