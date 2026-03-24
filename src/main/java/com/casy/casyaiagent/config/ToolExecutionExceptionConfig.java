package com.casy.casyaiagent.config;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class ToolExecutionExceptionConfig {
    private static final Logger logger = LoggerFactory.getLogger(ToolExecutionExceptionConfig.class);
    @Bean
    @Primary
    ToolExecutionExceptionProcessor toolExecutionExceptionProcessor() {
        return new DefaultToolExecutionExceptionProcessor(true) {
            @NotNull
            @Override
            public String process(@NotNull ToolExecutionException exception) {
                // 自定义日志记录
                logger.error("工具执行异常: ", exception.getCause());
                logger.error("异常工具: {}", exception.getToolDefinition().name());
                logger.error("异常描述: {}", exception.getToolDefinition().description());

                // 可以返回友好的错误信息给 AI
                return "工具执行失败: " + exception.getCause().getMessage();
            }
        };
    }
}