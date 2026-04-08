package com.casy.casyaiagent.agent;

import com.casy.casyaiagent.agent.model.AgentState;
import com.itextpdf.styledxmlparser.jsoup.internal.StringUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 抽象基础代理类，用于管理代理状态和执行流程。
 * <p>
 * 提供状态转换、内存管理和基于步骤的执行循环的基础功能。
 * 子类必须实现step方法。
 *
 * @author linlin
 */
@Data
@Slf4j
public abstract class BaseAgent {

    // 核心属性
    private String name;

    // 提示
    private String systemPrompt;
    private String nextStepPrompt;

    // 状态
    private AgentState state = AgentState.IDLE;

    // 执行控制
    private int maxSteps = 10;
    private int currentStep = 0;

    // LLM
    private ChatClient chatClient;

    // Memory（需要自主维护会话上下文），选使用内存存储
    private List<Message> messageList = new ArrayList<>();

    // 重复域值
    private int duplicateThreshold = 2;
    
    // 连续异常控制
    private int consecutiveErrorCount = 0;  // 当前连续异常次数
    private int maxConsecutiveErrors = 3;   // 最大允许的连续异常次数
    
    // 对话ID，用于保持对话记忆
    private String chatId;

    /**
     * 运行代理
     *
     * @param userPrompt 用户提示词
     * @return 执行结果
     */
    public String run(String userPrompt) {
        if (this.state != AgentState.IDLE) {
            throw new RuntimeException("无法从当前状态运行智能体: " + this.state);
        }
        if (StringUtil.isBlank(userPrompt)) {
            throw new RuntimeException("用户提示词为空，不能启动智能体执行任务");
        }
        // 更改状态
        state = AgentState.RUNNING;
        // 记录消息上下文
        messageList.add(new UserMessage(userPrompt));

        // 保存消息结果
        List<String> results = new ArrayList<>();

        try {
            // ========== 第一步：生成整体执行计划 ==========
            log.info("╔══════════════════════════════════════════════════════════════╗");
            log.info("║  [{}] 开始分析任务并制定执行计划", getName());
            log.info("╚══════════════════════════════════════════════════════════════╝");

            String initialPlan = generateInitialPlan(userPrompt);
            if (initialPlan != null && !initialPlan.isEmpty()) {
                log.info("📋 整体执行计划:\n{}", initialPlan);
                results.add("【执行计划】\n" + initialPlan);
            }

            log.info("╔══════════════════════════════════════════════════════════════╗");
            log.info("║  [{}] 开始执行具体步骤", getName());
            log.info("╚══════════════════════════════════════════════════════════════╝");

            // ========== 第二步：执行具体步骤 ==========
            for (int i = 0; i < maxSteps && state != AgentState.FINISHED; i++) {
                int stepNumber = i + 1;
                currentStep = stepNumber;
                log.info("执行步骤 {}/{}", stepNumber, maxSteps);
                // 单步执行
                String stepResult = step();
                // 每一步 step 执行完都要检查是否陷入循环
                if (isStuck()) {
                    handleStuckState();
                }
                String result = "Step " + stepNumber + ": " + stepResult;
                results.add(result);
            }
            // 检查是否超出步骤限制
            if (currentStep >= maxSteps) {
                state = AgentState.FINISHED;
                results.add("Terminated: Reached max steps (" + maxSteps + ")");
            }
            return String.join("\n", results);
        } catch (Exception e) {
            state = AgentState.ERROR;
            log.error("Error executing agent", e);
            return "执行错误" + e.getMessage();
        } finally {
            // 清理资源
            cleanup();
        }
    }

    /**
     * 运行代理（流式输出）
     * 整体流程
     * 步骤1: AI思考 → 调用 工具1 → 执行搜索
     * 步骤2: AI思考 → 调用 工具2 → 执行详情获取
     * 步骤3: AI思考 → 调用 doTerminate工具 → 设置 needFinalSummary=true ✅
     * 步骤4: 检测到 needFinalSummary → AI生成最终总结 → 设置 FINISHED ✅
     * 任务结束
     *
     * @param userPrompt 用户提示词
     * @return SseEmitter 实例
     */
    public SseEmitter runStream(String userPrompt) {
        // 创建SseEmitter，设置较长的超时时间
        SseEmitter sseEmitter = new SseEmitter(300000L); // 5分钟超时
        // 使用线程异步处理,避免阻塞主线程
        CompletableFuture.runAsync(() -> {
            try {
                // ✅ 添加超时回调
                sseEmitter.onTimeout(() -> {
                    this.state = AgentState.ERROR;
                    this.cleanup();
                    log.warn("SSE 连接超时");
                    try {
                        sseEmitter.send("会话连接超时: " + this.state);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    sseEmitter.complete();
                });
                // ✅ 添加连接断开回调
                sseEmitter.onCompletion(() -> {
                    if (this.state == AgentState.RUNNING) {
                        this.state = AgentState.FINISHED;
                    }
                    this.cleanup();
                    log.info("SSE 连接关闭");
                });

                if (this.state != AgentState.IDLE) {
                    sseEmitter.send("错误：无法从状态运行代理: " + this.state);
                    sseEmitter.complete();
                    return;
                }
                if (StringUtil.isBlank(userPrompt)) {
                    sseEmitter.send("错误：不能使用空提示词运行代理");
                    sseEmitter.complete();
                }
                // 更改状态
                state = AgentState.RUNNING;
                // 记录消息上下文
                messageList.add(new UserMessage(userPrompt));
                try {
                    // ========== 第一步：生成整体执行计划 ==========
                    log.info("[{}] 开始分析任务并制定执行计划", getName());

                    String initialPlan = generateInitialPlan(userPrompt);
                    if (initialPlan != null && !initialPlan.isEmpty()) {
                        log.info("📋 整体执行计划:\n{}", initialPlan);
                        sseEmitter.send("【执行计划】\n" + initialPlan);
                    }
                    log.info("[{}] 开始执行具体步骤", getName());
                    // ========== 第二步：执行具体步骤 ==========
                    for (int i = 0; i < maxSteps && state != AgentState.FINISHED && state != AgentState.ERROR; i++) {
                        int stepNumber = i + 1;
                        currentStep = stepNumber;
                        log.info("执行步骤 {}/{}", stepNumber, maxSteps);
                        sseEmitter.send("执行步骤 " + stepNumber + "/" + maxSteps);

                        // 单步执行
                        String stepResult = step();
                        // 每一步 step 执行完都要检查是否陷入循环
                        if (isStuck()) {
                            handleStuckState();
                        }
                        String result = "Step " + stepNumber + ": " + stepResult;
                        log.info("返回信息：{}", result);
                        // 发送每一步的结果
                        sseEmitter.send(result);
                    }
                    // 检查结束状态
                    if (currentStep >= maxSteps && state != AgentState.FINISHED) {
                        state = AgentState.FINISHED;
                        sseEmitter.send("执行结束: 达到最大步骤 (" + maxSteps + ")");
                    } else if (state == AgentState.ERROR) {
                        sseEmitter.send("执行结束: 智能体异常请稍后再试！");
                    } else if (state == AgentState.FINISHED) {
                        // 【新增】发送最后一条 AI 回复给前端
                        String finalReply = getLastAssistantReply();
                        if (finalReply != null && !finalReply.isEmpty()) {
                            sseEmitter.send(finalReply);
                        }
                        sseEmitter.send("执行结束: 任务已完成");
                    }
                    // 正常完成
                    sseEmitter.complete();
                } catch (Exception e) {
                    state = AgentState.ERROR;
                    log.error("执行智能体失败", e);
                    try {
                        sseEmitter.send("执行错误: " + e.getMessage());
                        sseEmitter.complete();
                    } catch (Exception ex) {
                        sseEmitter.completeWithError(ex);
                    }
                } finally {
                    // 清理资源
                    cleanup();
                }
            } catch (Exception e) {
                sseEmitter.completeWithError(e);
            }
        });
        return sseEmitter;
    }

    /**
     * 生成初始执行计划
     * <p>
     * 在正式开始执行任务前，AI 应该先分析任务并输出整体执行思路
     *
     * @param userPrompt 用户原始提示词
     * @return 执行计划描述
     */
    protected abstract String generateInitialPlan(String userPrompt);

    /**
     * 执行单个步骤
     *
     * @return 步骤执行结果
     */
    public abstract String step();
    
    /**
     * 增加连续异常计数
     */
    protected void incrementConsecutiveErrorCount() {
        this.consecutiveErrorCount++;
        log.warn("[{}] 连续异常次数: {}/{}", getName(), this.consecutiveErrorCount, this.maxConsecutiveErrors);
    }
    
    /**
     * 重置连续异常计数（在成功执行后调用）
     */
    protected void resetConsecutiveErrorCount() {
        if (this.consecutiveErrorCount > 0) {
            log.info("[{}] 重置连续异常计数", getName());
            this.consecutiveErrorCount = 0;
        }
    }


    /**
     * 获取最后一条 AI 助手的回复内容
     * 
     * @return 最后一条助手消息的内容，如果没有则返回 null
     */
    protected String getLastAssistantReply() {
        for (int i = messageList.size() - 1; i >= 0; i--) {
            Message msg = messageList.get(i);
            if (msg instanceof AssistantMessage) {
                AssistantMessage assistantMsg = (AssistantMessage) msg;
                String text = assistantMsg.getText();
                if (text != null && !text.isEmpty()) {
                    return text;
                }
            }
        }
        return null;
    }

    /**
     * 清理资源
     */
    protected void cleanup() {
        // 子类可以重写此方法来清理资源
    }

    /**
     * 处理陷入循环的状态
     */
    protected void handleStuckState() {
        String stuckPrompt = "观察到重复响应。考虑新策略，避免重复已尝试过的无效路径，若新策略无效应先通告用户原因，之后立即结束";
        this.nextStepPrompt = stuckPrompt + "\n" + (this.nextStepPrompt != null ? this.nextStepPrompt : "");
        System.out.println("Agent detected stuck state. Added prompt: " + stuckPrompt);
    }

    /**
     * 检查代理是否陷入循环
     * <p>
     * 检测逻辑：检查最近的助手消息是否有重复内容，
     * 如果在历史消息中发现相同内容的助手消息出现次数达到阈值，则认为陷入循环
     *
     * @return 是否陷入循环
     */
    protected boolean isStuck() {
        List<Message> messages = this.messageList;
        if (messages.size() < 2) {
            return false;
        }

        // 获取最近一条助手消息
        AssistantMessage lastAssistantMsg = null;
        for (int i = messages.size() - 1; i >= 0; i--) {
            Message msg = messages.get(i);
            if (msg instanceof AssistantMessage) {
                lastAssistantMsg = (AssistantMessage) msg;
                break;
            }
        }

        // 如果没有助手消息，或者助手消息没有内容（可能是工具调用），不判断为卡死
        if (lastAssistantMsg == null) {
            return false;
        }

        String lastContent = lastAssistantMsg.getText();
        // 如果助手消息是工具调用（文本为空），不判断为卡死
        if (lastContent == null || lastContent.isEmpty()) {
            return false;
        }

        // 计算相同内容的助手消息出现次数
        int duplicateCount = 0;
        for (Message msg : messages) {
            if (msg instanceof AssistantMessage assistantMsg) {
                String content = assistantMsg.getText();
                if (content != null && content.equals(lastContent)) {
                    duplicateCount++;
                }
            }
        }

        // 如果相同内容出现次数超过阈值（减去1是因为包含自己），则认为陷入循环
        boolean isStuck = duplicateCount > this.duplicateThreshold;
        if (isStuck) {
            log.warn("检测到循环状态：相同助手消息内容重复 {} 次", duplicateCount);
        }
        return isStuck;
    }
}
