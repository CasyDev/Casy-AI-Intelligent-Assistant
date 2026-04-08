package com.casy.casyaiagent.tool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * 请求用户输入工具
 * 当 AI 需要用户补充信息时调用此工具
 *
 * @author linlin
 * @version 1.0
 * @date 2026/4/7
 */
@Slf4j
@Component
public class RequestUserInputTool {

    // 存储用户输入的 future
    private static final ThreadLocal<CompletableFuture<String>> userInputFuture = new ThreadLocal<>();

    /**
     * 请求用户输入补充信息
     *
     * @param prompt 向用户展示的提示信息，说明需要补充什么信息
     * @return 用户输入的内容
     */
    @Tool(name = "requestUserInput", description = """
        当任务需要用户补充信息才能继续时，调用此工具向用户请求输入。
        例如：需要用户提供具体日期、地点、偏好、预算等信息时。
        
        使用场景：
        1. 用户说"帮我规划旅行"，但没有提供目的地、时间、预算等信息
        2. 用户说"查询天气"，但没有提供城市名称
        3. 用户说"搜索餐厅"，但没有提供位置或口味偏好
        
        参数 prompt 应该清晰说明需要什么信息，例如：
        - "请告诉我您的旅行目的地和出行日期"
        - "请提供您想查询的城市名称"
        - "请说明您的预算范围和用餐人数"
        """)
    public String requestUserInput(@ToolParam(description = "向用户展示的提示信息，说明需要补充什么信息") String prompt) {
        log.info("📝 AI 请求用户输入: {}", prompt);
        
        // 返回特殊格式的消息，前端会检测这个格式并显示输入框
        // 使用特殊标记 [USER_INPUT_REQUEST] 让前端识别
        return "[USER_INPUT_REQUEST]" + prompt + "[END_USER_INPUT_REQUEST]";
    }
}
