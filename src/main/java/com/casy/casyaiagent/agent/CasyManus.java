package com.casy.casyaiagent.agent;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.execution.ToolExecutionExceptionProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.UUID;

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

    // 依赖组件，用于重建 ChatClient
    private final ChatModel chatModel;
    private final ToolCallbackProvider toolCallbackProvider;
    private final boolean multiModel;

    public CasyManus(ToolCallback[] availableTools, ChatModel dashscopeChatModel,
                     ToolExecutionExceptionProcessor toolExecutionExceptionProcessor,
                     ToolCallbackProvider toolCallbackProvider,
                     @Value("${spring.ai.dashscope.chat.options.multi-model:false}") boolean multiModel) {
        super(availableTools, toolExecutionExceptionProcessor, multiModel);
        this.setName("casyManus");
        // 保存依赖，以便后续重建 ChatClient
        this.chatModel = dashscopeChatModel;
        this.toolCallbackProvider = toolCallbackProvider;
        this.multiModel = multiModel;

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
                
                【requestUserInput 工具 - 强制性要求】
                
                ⚠️ **重要：当你缺少必要信息时，必须调用 requestUserInput 工具，绝对不能只是口头提示用户！**
                
                什么情况下必须调用 requestUserInput：
                - 用户说"帮我规划旅行" → 你没有目的地、时间、预算信息 → **立即调用 requestUserInput**
                - 用户说"查询天气" → 你不知道城市名称 → **立即调用 requestUserInput**
                - 用户说"搜索餐厅" → 你不知道位置或偏好 → **立即调用 requestUserInput**
                - 任何情况下，如果你发现缺少必要信息才能继续 → **立即调用 requestUserInput**
                
                ❌ 错误做法（绝对禁止）：
                - 只是回复说"请告诉我您的旅行目的地..."
                - 只是回复说"我需要知道..."
                - 只是回复说"请提供..."
                
                ✅ 正确做法（必须执行）：
                - 直接调用 requestUserInput 工具
                - 参数 prompt 要清晰说明需要什么信息
                - 例如：prompt="请告诉我您的旅行目的地、出行日期和预算范围"
                
                【关键区别】
                - 口头回复：用户收不到提示，任务会继续执行，你会陷入循环
                - 调用工具：系统会暂停任务，前端会显示输入框让用户填写
                
                **记住：如果你回复的内容包含"请告诉我"、"请提供"、"我需要知道"等字样，说明你只是口头提示，这是错误的！**
                **正确的做法是立即调用 requestUserInput 工具！**
                
                【PDF 生成 - 必须遵守】
                用户要求生成 PDF、报告、摘要文档时，必须调用 generatePDF。
                禁止用 writeFile 把内容存成 HTML/TXT 然后声称任务完成。
                即使 generatePDF 报文件占用，也要换文件名再调用 generatePDF，不要改存 HTML。
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
                
                【再次提醒 - requestUserInput 工具】
                如果你发现缺少用户必要信息（如目的地、时间、预算等），**必须调用 requestUserInput 工具**！
                绝对不要只是口头说"请告诉我..."，那样工具不会被执行！
                
                【PDF】用户要 PDF 时必须调用 generatePDF，不要用 writeFile 保存 HTML 交差。
                生成成功后不要写 localhost:3000、磁盘路径或 /pdf/xxx.pdf 这类前端链接，前端会提供下载按钮。
                """, maxSteps);
        this.setNextStepPrompt(nextStepPrompt);

        this.setMaxSteps(maxSteps);
        
        // 初始化 ChatClient（不使用 MessageChatMemoryAdvisor，避免400错误）
        initChatClient();
        
        // 设置初始 chatId
        this.setChatId(UUID.randomUUID().toString());
    }

    private void initChatClient() {
        ChatClient.Builder builder = ChatClient.builder(chatModel)
//                .defaultAdvisors(new PromptLoggingAdvisor())
                .defaultToolCallbacks(toolCallbackProvider);
        if (multiModel) {
            builder.defaultOptions(DashScopeChatOptions.builder().multiModel(true).build());
        }
        this.setChatClient(builder.build());
    }
}
