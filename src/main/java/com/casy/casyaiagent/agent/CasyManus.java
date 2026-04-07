package com.casy.casyaiagent.agent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.execution.ToolExecutionExceptionProcessor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * CasyManus是可以直接提供给其他方法调用的AI超级智能体实例，
 * 继承自ToolCallAgent, 需要给智需要给智能体设置各种参数，
 * 比如对话客户端 chatClient、工具调用列表等
 *
 * @author linlin
 */
@Component
@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
public class CasyManus extends ToolCallAgent {

    public CasyManus(ToolCallback[] availableTools, ChatModel dashscopeChatModel, ToolExecutionExceptionProcessor toolExecutionExceptionProcessor,  ToolCallbackProvider toolCallbackProvider) {
        super(availableTools, toolExecutionExceptionProcessor);
        this.setName("casyManus");
        
        int maxSteps = 10;
        int planSteps = maxSteps / 3;
        int checkInterval = maxSteps / 2;
        
        String systemPrompt = String.format("""
                你是CasyManus，一款全能型人工智能助手，旨在处理用户提出的任何任务。
                你拥有多种可随时调用的工具，能够高效完成各类复杂的需求。
                
                【重要限制】你最多只能执行 %d 个步骤，请合理安排任务进度！
                
                工作流程：
                1. 计划阶段：首先分析任务需求，制定清晰的执行计划（控制在 %d 步内完成规划）
                2. 执行阶段：按照计划逐步执行，调用合适的工具
                3. 总结阶段：任务完成后，总结执行结果
                
                执行原则：
                - 先思考，后行动
                - 复杂任务要拆解为多个步骤，但必须控制在 %d 步以内
                - 每 %d 步检查一次进度，接近上限时加快执行或简化任务
                - 遇到问题及时调整策略，不要陷入无限循环
                - 步骤快用完时（剩余3步以内），必须优先完成任务或调用 terminate 结束
                
                【关键要求 - 必须遵守】
                1. **当你需要使用工具时，必须真正调用工具**，系统会自动执行并返回结果给你。
                2. **不要只是口头说** "调用工具：xxx" 或 "我将调用 xxx"，这样工具不会被执行！
                3. 如果你确实需要某个工具，直接调用它，系统会返回结果，然后你根据结果继续下一步。
                4. 当你决定结束任务时，必须**实际调用** doTerminate 工具，而不仅仅是口头说。只有实际调用工具才能真正结束任务！
                """, maxSteps, planSteps, maxSteps, checkInterval);
        this.setSystemPrompt(systemPrompt);
        
        String nextStepPrompt = String.format("""
                【步骤限制提醒】你最多只能执行 %d 个步骤，当前请合理安排！
                
                请根据当前执行进度，选择下一步行动：
                
                1. 如果需要调用工具来完成当前步骤，请**直接调用**相应工具（系统会自动执行并返回结果）
                2. 如果当前步骤已完成，请分析结果并决定下一步
                3. 如果任务已全部完成，请调用 doTerminate 工具结束
                4. 如果遇到困难或步骤快用完，请尽快完成任务或结束
                
                ⚠️ 重要提醒：
                - 说"调用工具：xxx"不会让工具执行，必须真正调用！
                - 调用工具后系统会自动返回结果，然后你继续下一步
                - 剩余步骤少于3步时，必须优先完成核心任务
                - 如果无法在剩余步骤内完成，请调用 terminate 结束并说明情况
                """, maxSteps);
        this.setNextStepPrompt(nextStepPrompt);
        
        this.setMaxSteps(maxSteps);
        // 初始化客户端
        ChatClient chatClient = ChatClient.builder(dashscopeChatModel)
//                .defaultAdvisors(new PromptLoggingAdvisor())
                .defaultToolCallbacks(toolCallbackProvider) // MCP
                .build();
        this.setChatClient(chatClient);
    }
}
