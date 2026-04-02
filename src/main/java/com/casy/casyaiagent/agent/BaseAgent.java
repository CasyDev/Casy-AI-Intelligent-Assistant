package com.casy.casyaiagent.agent;

import com.casy.casyaiagent.agent.model.AgentState;
import com.itextpdf.styledxmlparser.jsoup.internal.StringUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;

import java.util.List;

import java.util.ArrayList;

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
     * 清理资源
     */
    protected void cleanup() {
        // 子类可以重写此方法来清理资源
    }

    /**
     * 处理陷入循环的状态
     */
    protected void handleStuckState() {
        String stuckPrompt = "观察到重复响应。考虑新策略，避免重复已尝试过的无效路径，若新策略无较应先通告用户原因，之后立即结束，";
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
            if (msg instanceof AssistantMessage) {
                AssistantMessage assistantMsg = (AssistantMessage) msg;
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
