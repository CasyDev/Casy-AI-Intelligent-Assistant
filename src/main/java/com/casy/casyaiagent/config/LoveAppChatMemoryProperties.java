package com.casy.casyaiagent.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * LoveApp 对话记忆配置属性类
 * 用于读取 loveapp.chat.memory 相关配置
 *
 * @author linlin
 * @version 1.0
 * @date 2026/4/7
 */
@Data
@ConfigurationProperties(prefix = "loveapp.chat.memory")
public class LoveAppChatMemoryProperties {

    /**
     * 对话记忆存储类型
     * 可选值: in-memory (内存存储，无需数据库), jdbc (数据库存储)
     */
    private String type = "in-memory";

    /**
     * 最大保留消息数量
     */
    private int maxMessages = 10;
}
