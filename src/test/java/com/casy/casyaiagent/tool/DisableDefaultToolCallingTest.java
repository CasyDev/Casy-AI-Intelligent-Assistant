package com.casy.casyaiagent.tool;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.casy.casyaiagent.constant.Global;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.DefaultToolCallingManager;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.execution.ToolExecutionExceptionProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author linlin
 * @version 1.0
 * @description: 通过设置 ToolCallingChatOptions 的 internalToolExecutionEnabled 属性为 false 来禁用内部工具执行
 *              自己从 AI 的响应结果中提取工具调用列表，再依次执行
 * @date 2026/3/12 20:43
 */
@SpringBootTest
public class DisableDefaultToolCallingTest {

    @Value("${u_api_pro.api_key}")
    private String api_key;

    @Value("${search-api.api-key}")
    private String searchApiKey;

    @Resource
    private ToolExecutionExceptionProcessor toolExecutionExceptionProcessor; // 注入自定义处理器
    @Test
    void test() {
        // 配置不自动执行工具
        ChatOptions chatOptions = ToolCallingChatOptions.builder()
                .toolCallbacks(ToolCallbacks.from(new WeatherTools(api_key), new WebSearchTool(searchApiKey)))
                .internalToolExecutionEnabled(false)  // 禁用内部工具执行
                .build();
        // 创建工具调用管理器
        // 使用了 DefaultToolCallingManager.builder().build() 直接创建实例，这种方式不会使用 Spring 容器中定义的 ToolExecutionExceptionProcessor,需要手动配置
        ToolCallingManager toolCallingManager = DefaultToolCallingManager.builder().toolExecutionExceptionProcessor(toolExecutionExceptionProcessor).build();
        // 创建初始提示
        Prompt prompt = new Prompt("现在北京的天气如何？", chatOptions);
        // 发送请求给模型
        ChatResponse chatResponse = Global.getBean(DashScopeChatModel.class).call(prompt);
        // 手动处理工具调用循环
        while (chatResponse.hasToolCalls()) {
            // 执行工具调用
            ToolExecutionResult toolExecutionResult = toolCallingManager.executeToolCalls(prompt, chatResponse);
            // 创建包含工具结果的新提示
            prompt = new Prompt(toolExecutionResult.conversationHistory(), chatOptions);
            // 再次发送请求给模型
            chatResponse = Global.getBean(DashScopeChatModel.class).call(prompt);
        }
        // 获取最终回答
        System.out.println(chatResponse.getResult().getOutput().getText());
    }
}
