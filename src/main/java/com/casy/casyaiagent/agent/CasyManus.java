package com.casy.casyaiagent.agent;

import com.casy.casyaiagent.advisor.PromptLoggingAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Component;

/**
 * CasyManus是可以直接提供给其他方法调用的AI超级智能体实例，
 * 继承自ToolCallAgent, 需要给智需要给智能体设置各种参数，
 * 比如对话客户端 chatClient、工具调用列表等
 *
 * @author linlin
 */
@Component
public class CasyManus extends ToolCallAgent {

    public CasyManus(ToolCallback[] availableTools, ChatModel dashscopeChatModel) {
        super(availableTools);
        this.setName("casyManus");
        final String SYSTEM_PROMPT = """
                 You are CasyManus, an all-capable AI assistant, aimed at solving any task presented by the user. \s
                 You have various tools at your disposal that you can call upon to efficiently complete complex requests.
                """;
        this.setSystemPrompt(SYSTEM_PROMPT);
        /**
         * 根据用户需求，主动选择最合适的工具或工具组合。对于复杂的任务，你可以拆解问题，并一步步使用不同的工具来解决。使用每个工具后，清晰说明执行结果并建议下一步。如果你想在任何时候停止交互，可以使用“终止”工具函数调用。
         */
        String NEXT_STEP_PROMPT = """
                Based on user needs, proactively select the most appropriate tool or combination of tools.
                For complex tasks, you can break down the problem and use different tools step by step to solve it.
                After using each tool, clearly explain the execution results and suggest the next steps.
                If you want to stop the interaction at any point, use the `terminate` tool/function call.
                """;
        this.setNextStepPrompt(NEXT_STEP_PROMPT);
        this.setMaxSteps(20);
        // 初始化客户端
        ChatClient chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultAdvisors(new PromptLoggingAdvisor())
                .build();
        this.setChatClient(chatClient);
    }
}
