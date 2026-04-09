package com.casy.casyaiagent;

import com.casy.casyaiagent.util.GlobalSpringContextInitializer;
import org.springframework.ai.model.chat.memory.repository.jdbc.autoconfigure.JdbcChatMemoryRepositoryAutoConfiguration;
import org.springframework.ai.tool.execution.ToolExecutionExceptionProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;


/**
 * @author linlin
 */
@SpringBootApplication(
        exclude = {
                JdbcChatMemoryRepositoryAutoConfiguration.class,
                DataSourceAutoConfiguration.class  // 禁用数据源自动配置
        })
public class CasyAiAgentApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(CasyAiAgentApplication.class);
        // 手动添加自定义的初始化器
        application.addInitializers(new GlobalSpringContextInitializer());
        var context = application.run(args);
//        SpringApplication.run(CasyAiAgentApplication.class, args);

        var processors = context.getBeansOfType(ToolExecutionExceptionProcessor.class);
        System.out.println("找到的 ToolExecutionExceptionProcessor: " + processors.keySet());
    }

}
