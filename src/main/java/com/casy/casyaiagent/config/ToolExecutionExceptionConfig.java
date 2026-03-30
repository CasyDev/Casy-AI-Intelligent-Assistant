package com.casy.casyaiagent.config;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.ai.tool.execution.DefaultToolExecutionExceptionProcessor;
import org.springframework.ai.tool.execution.ToolExecutionException;
import org.springframework.ai.tool.execution.ToolExecutionExceptionProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @author linlin
 * @version 1.0
 * @description: 工具异常处理
 * @date 2026/3/13 20:37
 */
@Configuration
@Slf4j
public class ToolExecutionExceptionConfig {
    @Bean
    @Primary
    ToolExecutionExceptionProcessor toolExecutionExceptionProcessor() {
        return new DefaultToolExecutionExceptionProcessor(true) {
            @NotNull
            @Override
            public String process(@NotNull ToolExecutionException exception) {
                // 自定义日志记录
                log.error("工具执行异常: ", exception.getCause());
                log.error("异常工具: {}", exception.getToolDefinition().name());
                log.error("异常描述: {}", exception.getToolDefinition().description());

                // 可以返回友好的错误信息给 AI
                return "工具执行失败: " + exception.getCause().getMessage();
            }
        };
    }
}