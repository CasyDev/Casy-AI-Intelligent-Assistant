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
     * 检测 AI 是否只是口头上说要调用工具，但实际上没有生成 tool_calls
     * 例如 AI 说"调用工具：maps_around_search"但没有真正的工具调用
     * 
     * @param result AI 的思考结果文本
     * @return 是否只是口头上说调用工具
     */
    private boolean isToolCallMentionedButNotExecuted(String result) {
        if (result == null || result.isEmpty()) {
            return false;
        }
        String lowerResult = result.toLowerCase();
        // 检测常见的"口嗨"表达
        return (lowerResult.contains("调用工具") ||
                lowerResult.contains("我将调用") ||
                lowerResult.contains("我需要调用") ||
                lowerResult.contains("让我调用"));
    }
    
    /**
     * 检测 AI 是否只是口头上提示用户输入，但没有调用 requestUserInput 工具
     * 例如 AI 说"请告诉我您的目的地"但没有调用 requestUserInput 工具
     * 
     * @param result AI 的思考结果文本
     * @return 是否只是口头上提示用户输入
     */
    private boolean isUserInputRequestMentionedButNotExecuted(String result) {
        if (result == null || result.isEmpty()) {
            return false;
        }
        String lowerResult = result.toLowerCase();
        // 检测常见的请求用户输入的表达（但没有调用工具）
        boolean containsRequestPhrase = 
                lowerResult.contains("请告诉我") ||
                lowerResult.contains("请提供") ||
                lowerResult.contains("请说明") ||
                lowerResult.contains("我需要知道") ||
                lowerResult.contains("我需要您提供") ||
                lowerResult.contains("以便我为您") ||
                (lowerResult.contains("需要您") && lowerResult.contains("提供"));
        
        // 如果包含请求短语，但没有明确说明要调用工具，则认为是口头提示
        if (containsRequestPhrase) {
            // 排除已经明确说要调用工具的情况
            boolean explicitlyCallingTool = 
                    lowerResult.contains("调用 requestuserinput") ||
                    lowerResult.contains("调用requestuserinput") ||
                    lowerResult.contains("我将调用 requestuserinput");
            
            return !explicitlyCallingTool;
        }
        
        return false;
    }

    /**
     * 获取最后一条消息的内容（任何类型）
     * 
     * @return 最后一条消息的内容，如果没有则返回 null
     */
    private String getLastMessageContent() {
        List<Message> messages = getMessageList();
        if (messages.isEmpty()) {
            return null;
        }
        Message lastMsg = messages.get(messages.size() - 1);
        if (lastMsg instanceof AssistantMessage) {
            return ((AssistantMessage) lastMsg).getText();
        } else if (lastMsg instanceof ToolResponseMessage) {
            // 获取工具响应消息的内容
            ToolResponseMessage toolMsg = (ToolResponseMessage) lastMsg;
            if (!toolMsg.getResponses().isEmpty()) {
                return toolMsg.getResponses().get(0).responseData();
            }
        }
        return null;
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
        
        // 检查是否超过最大连续异常次数
        if (getConsecutiveErrorCount() >= getMaxConsecutiveErrors()) {
            log.error("[{}] 连续异常次数达到 {} 次，终止任务", getName(), getMaxConsecutiveErrors());
            setState(AgentState.ERROR);
            getMessageList().add(new SystemMessage("任务终止：连续多次调用工具失败，请稍后重试。"));
            return false;
        }
        
        // 【新增】检测是否陷入循环状态（AI 重复相同回复多次）
        // 当 AI 无法继续推进任务且重复提示用户补充信息时，自动结束任务
        if (isStuck()) {
            log.warn("[{}] 检测到 AI 陷入循环，自动结束任务并返回最后回复", getName());
            setState(AgentState.FINISHED);
            return false;
        }
        
        // 【新增】检测最后一条消息是否包含用户输入请求标记
        // 如果 AI 调用了 requestUserInput 工具，工具会返回特殊标记
        String lastMsgContent = getLastMessageContent();
        if (lastMsgContent != null && lastMsgContent.contains("[USER_INPUT_REQUEST]")) {
            log.info("[{}] 检测到用户输入请求，任务暂停等待用户输入", getName());
            setState(AgentState.FINISHED);
            return false;
        }
        
        // 【特殊处理】如果上一步调用了终止工具，现在生成最终总结
        if (needFinalSummary) {
            log.info("📝 生成任务最终总结...");
            
            // 添加系统消息提示 AI 生成最终总结
            getMessageList().add(new SystemMessage("""
                任务已完成。请根据以上所有信息，生成一个清晰、完整的最终回复给用户。
                总结任务执行结果，提供有用的信息或建议。直接输出回复内容，不需要调用任何工具。
                """));
            
            // 让 AI 生成最终回复（不启用工具调用）
            // 使用空的 chatOptions，不传递工具，避免 AI 再次调用工具
            Prompt finalPrompt = new Prompt(getMessageList());
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
            
            //  这里错了，如果有工具调用的话，不应该加 assistantMessage，而是让 act() 中的 executeToolCalls() 自动处理完整的消息序列（包括添加 assistantMessage 和 toolResponseMessage）
            //  否则只有tool_calls 后面没有对应的 tool 响应，会报错400
//          //  getMessageList().add(assistantMessage);

            if (toolCallList.isEmpty()) {
                // 不调用工具时
                log.info("💭 AI 思考结果: {}", result);
                
                // 【兜底机制1】检测 AI 只是口头上说要调用工具，但实际上没有生成 tool_calls 的情况
                if (isToolCallMentionedButNotExecuted(result)) {
                    log.warn("⚠️ AI 口头上说要调用工具但未实际调用，将提示 AI 真正调用工具");
                    // 添加系统消息提示 AI 需要真正调用工具
                    getMessageList().add(new SystemMessage("""
                        系统提示：你刚才说要调用工具，但实际上工具并未被执行。
                        请注意：口头描述"调用工具：xxx"不会让工具执行，必须真正调用工具。
                        如果你确实需要工具，请直接调用它；如果不需要，请继续分析当前已有的信息。
                        """));
                    return false;  // 返回 false，让下一步重新思考
                }
                
                // 【兜底机制2】检测 AI 只是口头提示用户输入但没有调用 requestUserInput 工具的情况
                if (isUserInputRequestMentionedButNotExecuted(result)) {
                    log.warn("⚠️ AI 口头提示需要用户输入但未调用 requestUserInput 工具，将强制要求调用工具");
                    // 添加系统消息强制 AI 调用 requestUserInput 工具
                    getMessageList().add(new SystemMessage("""
                        ⚠️ 系统警告：你只是口头提示用户需要提供信息，但没有调用 requestUserInput 工具！
                        
                        这是错误的！口头提示不会被用户看到，你必须调用 requestUserInput 工具才能暂停任务并显示输入框。
                        
                        请立即调用 requestUserInput 工具，传入清晰的 prompt 参数说明需要什么信息。
                        不要只是口头回复，必须真正调用工具！
                        """));
                    return false;  // 返回 false，让下一步重新思考并调用工具
                }
                
                // 【兜底机制3】检测 AI 表示要结束任务但实际上没有调用 terminate 工具的情况
                if (isTerminateIntention(result)) {
                    log.warn("⚠️ AI 表示要结束任务但未实际调用 terminate 工具，将生成最终总结后结束");
                    needFinalSummary = true;  // 设置标记，让下一步生成最终总结
                    return false;
                }
                
                log.info("✅ 任务完成，无需调用工具");
                // 不调用工具时，直接添加 AI 的回复到消息历史
                getMessageList().add(assistantMessage);
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
                        if (!args.isEmpty()) {
                            log.info("  │   意图: AI 准备使用 {} 工具执行操作", toolCall.name());
                        }
                    } catch (Exception e) {
                        // 忽略解析错误
                    }
                }
                return true;
            }
        } catch (Exception e) {
            /**
             * 解决400报错，An assistant message with "tool_calls" must be followed by tool messages responding to each "tool_call_id"
             * 报错日志：[casy-ai-agent] [onPool-worker-4] c.casy.casyaiagent.agent.ToolCallAgent :
             * 🤔 AI 正在思考... 2026-04-07T11:59:30.399+08:00 WARN 3728 --- [casy-ai-agent] [onPool-worker-4] o.s.a.r.a.SpringAiRetryAutoConfiguration :
             * Retry error. Retry count: 1, Exception:
             * HTTP 400 - {"request_id":"fb41f7f7-2ec6-9499-a261-8367cb2f0f1c","code":"InvalidParameter",
             * "message":"<400> InternalError.Algo.InvalidParameter: An assistant message with "tool_calls" must be followed by tool messages responding to each "tool_call_id".
             * The following tool_call_ids did not have response messages: message[5].role"}
             *
             * 原因是 OpenAI / DashScope 等模型 API 的严格消息顺序要求
             * 当 assistant 消息包含 tool_calls 时，
             * 下一个消息必须是 tool 类型的响应（对应每个 tool_call_id），
             * 然后才能是下一个 assistant 消息
             *
             * 原代码是：    getMessageList().add(new AssistantMessage("处理时遇到错误: " + e.getMessage()));
             * 导致步骤变成了
             * [0] system: "你是一个智能助手..."
             * [1] user: "北京丰台站附近有什么好吃的？"
             * [2] assistant (带 tool_calls): {
             *       "tool_calls": [{"id": "call_abc123", ...}]  // ← AI 想调用工具
             *     }
             * [3] assistant: "处理时遇到错误: HTTP 400..."   // ❌ 错误！又一个 assistant！
             *                                                // 期望的是 tool 响应
             *
             * 要使用，system 消息不会干扰 tool_calls 的执行流程
             *
             *
             */
            log.error("{}的思考过程遇到了问题: {}", getName(), e.getMessage(), e);
            // 【修复】使用 SystemMessage 而非 AssistantMessage，避免破坏 tool_calls 消息顺序
            getMessageList().add(new SystemMessage("系统提示：AI 处理时遇到错误: " + e.getMessage() + "。请重试或调整策略。"));
            // 增加连续异常计数
            incrementConsecutiveErrorCount();
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
        
        try {
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
            
            // 【新增】当调用了 requestUserInput 工具时，立即结束任务等待用户输入
            if (toolResponseMessage.getResponses().stream().anyMatch(res -> StrUtil.equals("requestUserInput", res.name()))) {
                log.info("📝 检测到用户输入请求，任务暂停等待用户输入");
                // 获取工具返回的提示信息
                String inputPrompt = toolResponseMessage.getResponses().stream()
                    .filter(res -> StrUtil.equals("requestUserInput", res.name()))
                    .findFirst()
                    .map(res -> res.responseData())
                    .orElse("请提供补充信息");
                // 设置状态为 FINISHED，结束当前任务
                setState(AgentState.FINISHED);
                return "等待用户输入: " + inputPrompt;
            }
            
            // 【关键】重置连续异常计数，工具调用成功
            resetConsecutiveErrorCount();
            
            log.info("✅ 工具调用执行完成");
            return results;
        } catch (Exception e) {
            log.error("[{}] 工具执行异常: {}", getName(), e.getMessage(), e);
            // 增加连续异常计数
            incrementConsecutiveErrorCount();
            // 添加系统消息通知 AI 工具执行失败
            getMessageList().add(new SystemMessage("系统提示：工具执行失败，错误信息: " + e.getMessage() + "。请检查参数后重试或更换工具。"));
            return "工具执行失败: " + e.getMessage();
        }
    }
}
