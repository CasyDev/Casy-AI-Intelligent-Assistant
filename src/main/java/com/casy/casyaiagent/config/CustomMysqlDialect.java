package com.casy.casyaiagent.config;

import jakarta.annotation.PostConstruct;
import org.jetbrains.annotations.NotNull;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepositoryDialect;
import org.springframework.stereotype.Component;

/**
 * 自定义的PostgreSQL方言实现，支持自定义表名。
 * 本实现假设表结构包含以下字段：id, conversation_id, message_order, role, content, created_at
 * @author linlin
 */
@Component // 可注册为Spring组件，方便注入
public class CustomMysqlDialect implements JdbcChatMemoryRepositoryDialect {

    private final String tableName;

    /**
     * 构造方法。
     * @param tableName 自定义的表名，例如 "my_chat_history"。
     */
    public CustomMysqlDialect(String tableName) {
        this.tableName = tableName;
    }

    // 【关键】以下四个方法是接口要求必须实现的，返回操作指定表名的SQL

    @NotNull
    @Override
    public String getSelectMessagesSql() {
        // 按对话ID查询，并按顺序排序
        return String.format(
                "SELECT id, conversation_id, message_order, role, content, created_at FROM %s WHERE conversation_id = ? ORDER BY message_order ASC",
                tableName
        );
    }

    @NotNull
    @Override
    public String getInsertMessageSql() {
        // 插入一条对话记录
        return String.format(
                "INSERT INTO %s (conversation_id, message_order, role, content, created_at) VALUES (?, ?, ?, ?, ?)",
                tableName
        );
    }

    @NotNull
    @Override
    public String getSelectConversationIdsSql() {
        // 查询所有存在的会话ID（用于可能的管理或清理功能）
        return String.format("SELECT DISTINCT conversation_id FROM %s", tableName);
    }

    @NotNull
    @Override
    public String getDeleteMessagesSql() {
        // 根据会话ID删除所有相关消息
        return String.format("DELETE FROM %s WHERE conversation_id = ?", tableName);
    }

    /**
     * 【强烈建议】提供一个初始化建表的方法。
     * 注意：此方法不应定义在接口中，是您为了方便管理而添加的。
     * 生产环境建议使用Flyway/Liquibase等数据库迁移工具。
     */
    @PostConstruct
    public String getCreateTableSql() {
        return String.format("""
            CREATE TABLE IF NOT EXISTS %s (
                id BIGSERIAL PRIMARY KEY,
                conversation_id VARCHAR(255) NOT NULL,
                message_order INT NOT NULL,
                role VARCHAR(50) NOT NULL,
                content TEXT NOT NULL,
                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                CONSTRAINT unique_order_per_conversation UNIQUE (conversation_id, message_order)
            )
            """, tableName);
    }

    /**
     * 【强烈建议】提供创建索引的SQL。
     */
    public String getCreateIndexSql() {
        return String.format("CREATE INDEX IF NOT EXISTS idx_%s_cid ON %s (conversation_id)", tableName, tableName);
    }
}