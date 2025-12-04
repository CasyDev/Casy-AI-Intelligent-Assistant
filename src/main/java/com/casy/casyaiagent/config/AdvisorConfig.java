package com.casy.casyaiagent.config;
 
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdvisorConfig {

    // 注入您上一步定义的ChatMemory Bean
    @Bean
    public MessageWindowChatMemory chatMemoryAdvisor(ChatMemory chatMysqlMemory) {
        return 
    }
}