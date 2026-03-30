package com.casy.casyaiagent.agent;

import com.casy.casyaiagent.advisor.PromptLoggingAdvisor;
import com.casy.casyaiagent.agent.model.AgentState;
import com.itextpdf.styledxmlparser.jsoup.internal.StringUtil;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * 抽象基础代理类，用于管理代理状态和执行流程。
 *
 * 提供状态转换、内存管理和基于步骤的执行循环的基础功能。
 * 子类必须实现step方法。
 * @author linlin
 */
@Data
//@Slf4j
public abstract class BaseAgent {

    // TODO idea升级后修改
    private static final Logger log = LoggerFactory.getLogger(BaseAgent.class);

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
            // 没有超时最大步骤，并且状态并非完成状态
            for (int i = 0 ; i < maxSteps && state != AgentState.FINISHED; i++) {
                int stepNumber = i + 1;
                currentStep = stepNumber;
                log.info("执行步骤 {}/{}", stepNumber, maxSteps);
                // 单步执行
                String stepResult = step();
                String result = "Step " + stepNumber + ": " + stepResult;
                results.add(result);
            }
            // 检查是否超出步骤限制
            if (currentStep >= maxSteps) {
                state = AgentState.FINISHED;
                results.add("Terminated: Reached max steps (" + maxSteps + ")");
            }
            return String.join("\n", results);
        } catch(Exception e) {
            state = AgentState.ERROR;
            log.error("Error executing agent", e);
            return "执行错误" + e.getMessage();
        } finally {
            // 清理资源
            cleanup();
        }
    }

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

}
