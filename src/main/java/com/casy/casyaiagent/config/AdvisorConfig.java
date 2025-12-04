package com.casy.casyaiagent.config;
 
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdvisorConfig {

    @Bean
    public MessageChatMemoryAdvisor chatMemoryAdvisor(ChatMemory chatMysqlMemory) {
        return MessageChatMemoryAdvisor.builder(chatMysqlMemory).build();
    }
}