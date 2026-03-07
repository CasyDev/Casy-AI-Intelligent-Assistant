package com.casy.casyaiagent;

import com.casy.casyaiagent.util.GlobalSpringContextInitializer;
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
        SpringApplication application = new SpringApplication(CasyAiAgentApplication.class);
        // 手动添加自定义的初始化器
        application.addInitializers(new GlobalSpringContextInitializer());
        application.run(args);
//        SpringApplication.run(CasyAiAgentApplication.class, args);
    }

}
