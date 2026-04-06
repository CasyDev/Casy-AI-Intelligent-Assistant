package com.casy.casyaiagent.agent;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.casy.casyaiagent.agent.model.AgentState;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.DefaultToolCallingManager;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.execution.ToolExecutionExceptionProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ToolCallAgent 负责实现工具调用能力，继承自 ReActAgent，具体实现了 think 和 act 两个抽象方法。
 *
 * @author linlin
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class ToolCallAgent extends ReActAgent {

    // 可用的工具
    private final ToolCallback[] availableTools;
    // 工具调用管理者
    private final ToolCallingManager toolCallingManager;
    // 禁用内置的工具调用机制，自己维护上下文
    private final ChatOptions chatOptions;
    // 保存了工具调用信息的响应
    private ChatResponse toolCallChatResponse;
    // 标记是否需要生成最终总结（调用终止工具后需要再生成一次总结回复）
    private boolean needFinalSummary = false;

    public ToolCallAgent(ToolCallback[] availableTools, ToolExecutionExceptionProcessor toolExecutionExceptionProcessor) {
        super();
        this.availableTools = availableTools;
        this.toolCallingManager = DefaultToolCallingManager.builder().toolExecutionExceptionProcessor(toolExecutionExceptionProcessor).build();
        // 禁用 Spring AI 内置的工具调用机制，自己维护选项和消息上下文
        this.chatOptions = DashScopeChatOptions.builder().toolCallbacks(List.of(this.availableTools)).internalToolExecutionEnabled(false)  // 禁用内部工具执行
                .build();
    }

    /**
     * 检测 AI 回复是否包含结束任务的意图
     * 用于处理 AI 口头上说"调用工具：terminate"但实际上没有生成工具调用的情况
     * 
     * @param result AI 的思考结果文本
     * @return 是否包含结束意图
     */
    private boolean isTerminateIntention(String result) {
        if (result == null || result.isEmpty()) {
            return false;
        }
        String lowerResult = result.toLowerCase();
        // 检测常见的结束任务表达
        return (lowerResult.contains("terminate") ||
                lowerResult.contains("结束任务"));
    }

    /**
     * 生成初始执行计划
     */
    @Override
    protected String generateInitialPlan(String userPrompt) {
        try {
            // 构建计划生成的系统提示词
            String planSystemPrompt = """
                你是一位专业的任务规划专家。请分析用户的任务需求，并输出一份清晰的执行计划。
                
                计划应包含以下内容：
                1. 任务目标：简要说明任务的核心目标
                2. 执行步骤：列出完成此任务需要的主要步骤
                3. 所需工具：列出可能需要使用的工具
                4. 预期成果：说明任务完成后的输出
                
                注意：
                - 只输出计划，不要开始执行
                - 使用清晰的编号和列表格式
                - 如果任务简单，可以简化计划
                """;
            
            // 构建请求，获取计划
            String planPrompt = "请为以下任务制定执行计划：\n\n" + userPrompt;
            
            String plan = getChatClient().prompt()
                    .system(planSystemPrompt)
                    .user(planPrompt)
                    .call()
                    .content();
            
            // 将计划添加到消息历史（作为系统消息）
            if (plan != null && !plan.isEmpty()) {
                getMessageList().add(new SystemMessage("已制定的执行计划：\n" + plan));
            }
            
            return plan;
        } catch (Exception e) {
            log.warn("生成初始计划时出错: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 处理当前状态并决定下一步行动
     *
     * @return 是否需要执行行动
     */
    @Override
    public boolean think() {
        log.info("╔══════════════════════════════════════════════════════════════╗");
        log.info("║  [{}] 开始第 {} 步思考", getName(), getCurrentStep());
        log.info("╚══════════════════════════════════════════════════════════════╝");
        
        // 【特殊处理】如果上一步调用了终止工具，现在生成最终总结
        if (needFinalSummary) {
            log.info("📝 生成任务最终总结...");
            
            // 添加系统消息提示 AI 生成最终总结
            getMessageList().add(new SystemMessage("""
                任务已完成。请根据以上所有信息，生成一个清晰、完整的最终回复给用户。
                总结任务执行结果，提供有用的信息或建议。直接输出回复内容，不需要调用任何工具。
                """));
            
            // 让 AI 生成最终回复（不启用工具调用）
            Prompt finalPrompt = new Prompt(getMessageList(), chatOptions);
            ChatResponse finalResponse = getChatClient().prompt(finalPrompt).system(getSystemPrompt()).call().chatResponse();
            AssistantMessage finalMessage = finalResponse.getResult().getOutput();
            
            // 记录最终回复
            getMessageList().add(finalMessage);
            log.info("✅ 最终回复: {}", finalMessage.getText());
            
            // 真正结束任务
            setState(AgentState.FINISHED);
            needFinalSummary = false;
            return false;
        }
        
        if (getNextStepPrompt() != null && !getNextStepPrompt().isEmpty()) {
            UserMessage userMessage = new UserMessage(getNextStepPrompt());
            getMessageList().add(userMessage);
            log.info("📋 步骤提示: {}", getNextStepPrompt());
        }
        
        // 拿到历史消息
        List<Message> messageList = getMessageList();
        log.debug("📚 当前对话历史消息数: {}", messageList.size());
        Prompt prompt = new Prompt(messageList, chatOptions);

        try {
            log.info("🤔 AI 正在思考...");
            // 获取带工具选项的响应
            // 记录响应，用于 Act
            this.toolCallChatResponse = getChatClient().prompt(prompt).system(getSystemPrompt()).toolCallbacks(availableTools).call().chatResponse();
            AssistantMessage assistantMessage = this.toolCallChatResponse.getResult().getOutput();
            
            // 输出提示信息
            String result = assistantMessage.getText();
            List<AssistantMessage.ToolCall> toolCallList = assistantMessage.getToolCalls();
            
            // 【关键修复】始终将 AI 的思考结果添加到消息历史，无论是否调用工具
            // 这样才能确保客户端能收到 AI 的回复内容
            getMessageList().add(assistantMessage);
            
            if (toolCallList.isEmpty()) {
                // 不调用工具时
                log.info("💭 AI 思考结果: {}", result);
                
                // 【兜底机制】检测 AI 表示要结束任务但实际上没有调用 terminate 工具的情况
                if (isTerminateIntention(result)) {
                    log.warn("⚠️ AI 表示要结束任务但未实际调用 terminate 工具，将生成最终总结后结束");
                    needFinalSummary = true;  // 设置标记，让下一步生成最终总结
                    return false;
                }
                
                log.info("✅ 任务完成，无需调用工具");
                return false;
            } else {
                // 需要调用工具时，记录 AI 的决策过程
                log.info("🔧 AI 决定调用工具来解决问题");
                log.info("📊 工具调用数量: {} 个", toolCallList.size());
                
                // 记录每个工具的详细信息
                for (int i = 0; i < toolCallList.size(); i++) {
                    AssistantMessage.ToolCall toolCall = toolCallList.get(i);
                    log.info("  ├─ 工具 [{}]: {}", i + 1, toolCall.name());
                    log.info("  │   参数: {}", toolCall.arguments());
                    
                    // 解析参数并记录更友好的信息
                    try {
                        String args = toolCall.arguments();
                        if (args != null && !args.isEmpty()) {
                            log.info("  │   意图: AI 准备使用 {} 工具执行操作", toolCall.name());
                        }
                    } catch (Exception e) {
                        // 忽略解析错误
                    }
                }
                return true;
            }
        } catch (Exception e) {
            log.error(getName() + "的思考过程遇到了问题: " + e.getMessage(), e);
            getMessageList().add(new AssistantMessage("处理时遇到错误: " + e.getMessage()));
            return false;
        }
    }

    /**
     * 执行工具调用并处理结果
     *
     * @return 执行结果
     */
    @Override
    public String act() {
        log.info("╔══════════════════════════════════════════════════════════════╗");
        log.info("║  [{}] 开始执行工具调用", getName());
        log.info("╚══════════════════════════════════════════════════════════════╝");
        
        if (!toolCallChatResponse.hasToolCalls()) {
            log.warn("⚠️ 没有需要调用的工具");
            return "没有工具调用";
        }
        
        // 调用工具
        log.info("🚀 正在执行工具调用...");
        Prompt prompt = new Prompt(getMessageList(), chatOptions);
        ToolExecutionResult toolExecutionResult = toolCallingManager.executeToolCalls(prompt, toolCallChatResponse);
        
        // 记录消息上下文，conversationHistory 已经包含了助手消息和工具调用返回的结果
        setMessageList(toolExecutionResult.conversationHistory());
        
        // 当前工具调用的结果
        ToolResponseMessage toolResponseMessage = (ToolResponseMessage) CollUtil.getLast(toolExecutionResult.conversationHistory());
        
        // 记录每个工具的执行结果
        log.info("📈 工具执行结果:");
        for (int i = 0; i < toolResponseMessage.getResponses().size(); i++) {
            ToolResponseMessage.ToolResponse response = toolResponseMessage.getResponses().get(i);
            String resultData = response.responseData();
            // 截断过长的结果
            String displayResult = resultData != null && resultData.length() > 200 
                ? resultData.substring(0, 200) + "... (共" + resultData.length() + "字符)" 
                : resultData;
            log.info("  ├─ 工具 [{}]: {}", i + 1, response.name());
            log.info("  │   结果: {}", displayResult);
        }
        
        String results = toolResponseMessage.getResponses().stream()
            .map(res -> "工具 " + res.name() + " 执行完成，结果: " + res.responseData())
            .collect(Collectors.joining("\n"));
        
        // 当调用了终止工具时，设置标记让下一步生成最终总结
        if (toolResponseMessage.getResponses().stream().anyMatch(res -> StrUtil.equals("doTerminate", res.name()))) {
            log.info("🏁 检测到终止工具调用，下一步将生成最终总结");
            needFinalSummary = true;
            return "任务结束，准备生成最终总结";
        }
        
        log.info("✅ 工具调用执行完成");
        return results;
    }
}
