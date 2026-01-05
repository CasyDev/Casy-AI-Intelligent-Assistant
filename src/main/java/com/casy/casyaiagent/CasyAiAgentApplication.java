package com.casy.casyaiagent;

import org.springframework.ai.model.chat.memory.repository.jdbc.autoconfigure.JdbcChatMemoryRepositoryAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * @author linlin
 */
@SpringBootApplication(
        exclude = {JdbcChatMemoryRepositoryAutoConfiguration.class})
public class CasyAiAgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(CasyAiAgentApplication.class, args);
    }

}
