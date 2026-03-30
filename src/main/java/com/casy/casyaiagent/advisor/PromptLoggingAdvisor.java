package com.casy.casyaiagent.advisor;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.metadata.ChatResponseMetadata;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 完整的提示词+响应日志 Advisor (Spring AI 1.1.0 兼容版本)
 * 在 adviseCall 方法中完成请求拦截、执行和日志记录。
 */
@Slf4j
public class PromptLoggingAdvisor implements CallAdvisor { // 【修正1】实现 CallAdvisor 而非 BaseAdvisor

    private static final DateTimeFormatter DEFAULT_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    // 扩展日志模板
    private static final String DEFAULT_FULL_LOG_TEMPLATE = """
            【AI交互日志】
            ├─ 基础信息
            │  ├─ 时间戳: {timestamp}
            │  ├─ 模型标识: {modelIdentifier}
            │  └─ 响应耗时: {responseTimeMs} ms
            ├─ 请求信息
            │  ├─ 原始提示词: {originalPrompt}
            │  └─ 提示词长度: {promptLength} 字符
            ├─ 响应信息
            │  ├─ 响应内容: {responseContent}
            │  ├─ 响应长度: {responseLength} 字符
            │  └─ 响应状态: {responseStatus}
            └─ Token 消耗（如有）
               ├─ 提示词Token: {promptTokens}
               ├─ 响应Token: {completionTokens}
               └─ 总Token: {totalTokens}
            """;
    // 【修正2】使用线程安全的Map来临时存储请求开始时间，解决 before/after 数据传递问题
    private static final Map<ChatClientRequest, Long> REQUEST_START_TIME_CACHE = new ConcurrentHashMap<>();
    // 模型别名映射（可选功能）
    private static final Map<String, String> MODEL_ALIAS_MAP = new HashMap<>();

    static {
        MODEL_ALIAS_MAP.put("gpt-3.5-turbo-0125", "gpt-3.5");
        MODEL_ALIAS_MAP.put("qwen-turbo-2024-04-01", "千问-turbo");
        // 可根据需要添加更多映射
    }

    // 配置项
    private final String logTemplate;
    private final DateTimeFormatter dateFormatter;
    private final boolean enableLogging;
    private final boolean recordTokenInfo;
    private int order = 0;

    // ==================== 构造器 ====================
    public PromptLoggingAdvisor() {
        this(DEFAULT_FULL_LOG_TEMPLATE, DEFAULT_DATE_FORMATTER, true, true);
    }

    public PromptLoggingAdvisor(String logTemplate) {
        this(logTemplate, DEFAULT_DATE_FORMATTER, true, true);
    }

    public PromptLoggingAdvisor(String logTemplate, DateTimeFormatter dateFormatter,
                                boolean enableLogging, boolean recordTokenInfo) {
        this.logTemplate = logTemplate;
        this.dateFormatter = dateFormatter;
        this.enableLogging = enableLogging;
        this.recordTokenInfo = recordTokenInfo;
    }

    // ==================== 核心方法：实现 CallAdvisor 接口 ====================
    @NotNull
    @Override
    public ChatClientResponse adviseCall(@NonNull ChatClientRequest request, @NonNull CallAdvisorChain chain) {
        // 如果不启用日志，直接传递请求
        if (!enableLogging) {
            return chain.nextCall(request);
        }

        long startTime = System.currentTimeMillis();
        // 将开始时间与当前请求关联
        REQUEST_START_TIME_CACHE.put(request, startTime);

        try {
            // 执行真正的AI调用链
            ChatClientResponse response = chain.nextCall(request);

            // 调用完成后，记录日志
            logRequestAndResponse(request, response, startTime);
            return response;
        } catch (Exception e) {
            // 即使调用失败也尝试记录请求信息
            log.error("AI调用发生异常，请求信息：{}", getOriginalPrompt(request), e);
            throw e; // 重新抛出异常
        } finally {
            // 清理缓存，防止内存泄漏
            REQUEST_START_TIME_CACHE.remove(request);
        }
    }

    // ==================== 私有方法：提取信息和记录日志 ====================
    private void logRequestAndResponse(ChatClientRequest request, ChatClientResponse response, long startTime) {
        try {
            long responseTimeMs = System.currentTimeMillis() - startTime;
            String originalPrompt = getOriginalPrompt(request);
            int promptLength = originalPrompt.length();
            String modelIdentifier = getModelIdentifier(request, response);
            String responseContent = getResponseContent(response);
            int responseLength = responseContent.length();
            String responseStatus = getResponseStatus(response);
            Map<String, Object> tokenInfo = recordTokenInfo ? getTokenInfo(response) : new HashMap<>();

            // 渲染完整日志模板
            Map<String, Object> logVariables = new HashMap<>();
            logVariables.put("timestamp", LocalDateTime.now().format(dateFormatter));
            logVariables.put("modelIdentifier", modelIdentifier);
            logVariables.put("responseTimeMs", String.valueOf(responseTimeMs)); // long -> String
            logVariables.put("originalPrompt", originalPrompt);
            logVariables.put("promptLength", String.valueOf(promptLength)); // int -> String
            logVariables.put("responseContent", responseContent);
            logVariables.put("responseLength", String.valueOf(responseLength)); // int -> String
            logVariables.put("responseStatus", responseStatus);
            // 确保tokenInfo的值也是String，这里getOrDefault本身返回Object，需转换
            logVariables.put("promptTokens", tokenInfo.getOrDefault("promptTokens", "未统计").toString());
            logVariables.put("completionTokens", tokenInfo.getOrDefault("completionTokens", "未统计").toString());
            logVariables.put("totalTokens", tokenInfo.getOrDefault("totalTokens", "未统计").toString());
            // 渲染完整日志模板
            String logContent = PromptTemplate.builder()
                    .template(logTemplate)
                    .variables(logVariables)
                    .build()
                    .render();

            log.info(logContent);
        } catch (Exception e) {
            log.error("记录AI交互日志时发生异常", e);
        }
    }

    /**
     * 获取原始提示词
     */
    private String getOriginalPrompt(ChatClientRequest request) {
        if (request.prompt().getUserMessage() == null) {
            return "[空提示词]";
        }
        String text = request.prompt().getUserMessage().getText();
        return text.trim().isEmpty() ? "[空提示词]" : text.trim();
    }

    /**
     * 获取模型标识（综合请求和响应信息）
     */
    private String getModelIdentifier(ChatClientRequest request, ChatClientResponse response) {
        // 1. 优先从响应元数据中获取（最准确）
        String modelFromResponse = response.chatResponse().getMetadata().get("model");
        if (modelFromResponse != null && !modelFromResponse.trim().isEmpty()) {
            return applyModelAlias(modelFromResponse.trim());
        }

        // 2. 尝试从请求属性中获取
        String model = request.prompt().getOptions().getModel();
        if (StrUtil.isNotBlank(model)) {
            return model;
        }
        // 3. 最后返回通用标识
        return "未知模型";
    }

    /**
     * 应用模型别名映射
     */
    private String applyModelAlias(String rawModelName) {
        return MODEL_ALIAS_MAP.getOrDefault(rawModelName, rawModelName);
    }

    /**
     * 提取响应文本
     */
    private String getResponseContent(ChatClientResponse response) {
        try {
            return response.chatResponse().getResult().getOutput().getText();
        } catch (Exception e) {
            return "[无法提取响应内容]";
        }
    }

    /**
     * 判断响应状态
     */
    private String getResponseStatus(ChatClientResponse response) {
        try {
            // 1. 尝试获取底层ChatResponse的元数据
            ChatResponseMetadata metadata = response.chatResponse().getMetadata();

            // 检查元数据中是否有明确的错误信息或状态码
            if (metadata != null) {
                String finishReason = (String) metadata.get("finishReason");
                // 一些服务商可能会返回错误码，如 "error" 或 "invalid_request"
                String errorCode = (String) metadata.get("error");

                if ("ERROR".equalsIgnoreCase(finishReason) || errorCode != null) {
                    return String.format("失败 - 原因: %s, 错误码: %s", finishReason, errorCode);
                }
                // 如果元数据中包含 "STOP"，通常表示正常结束
                if ("STOP".equalsIgnoreCase(finishReason)) {
                    return "成功";
                }
            }

            // 2. 最终兜底检查：响应内容是否有效
            String content = getResponseContent(response); // 使用您已实现的提取内容的方法
            if (content == null || content.trim().isEmpty() || content.contains("[无法提取响应内容]")) {
                return "失败（无有效响应内容）";
            }

            // 3. 如果以上都通过了，则认为成功
            return "成功";

        } catch (Exception e) {
            // 如果在检查过程中出现任何异常，也视为失败
            return String.format("失败（状态检查异常: %s）", e.getMessage());
        }
    }

    /**
     * 提取Token消耗信息
     */
    private Map<String, Object> getTokenInfo(ChatClientResponse response) {
        Map<String, Object> tokenInfo = new HashMap<>();
        try {
            Usage usage = response.chatResponse().getMetadata().getUsage();
            tokenInfo.put("promptTokens", usage.getPromptTokens());
            tokenInfo.put("completionTokens", usage.getCompletionTokens());
            tokenInfo.put("totalTokens", usage.getTotalTokens());
        } catch (Exception e) {
            tokenInfo.put("promptTokens", "统计失败");
            tokenInfo.put("completionTokens", "统计失败");
            tokenInfo.put("totalTokens", "统计失败");
        }
        return tokenInfo;
    }

    // ==================== 实现 CallAdvisor 接口的其他方法 ====================
    @Override
    public String getName() {
        return "PromptLoggingAdvisor";
    }

    @Override
    public int getOrder() {
        return this.order;
    }

    // ==================== 链式配置方法（返回新实例，避免修改final字段）====================

    /**
     * 设置执行顺序（数字越小优先级越高）
     */
    public PromptLoggingAdvisor withOrder(int order) {
        PromptLoggingAdvisor newAdvisor = new PromptLoggingAdvisor(
                this.logTemplate, this.dateFormatter, this.enableLogging, this.recordTokenInfo
        );
        newAdvisor.order = order; // 修改新实例的order字段
        return newAdvisor;
    }

    /**
     * 创建并返回一个禁用了日志的新实例
     */
    public PromptLoggingAdvisor disableLogging() {
        return new PromptLoggingAdvisor(
                this.logTemplate, this.dateFormatter, false, this.recordTokenInfo
        );
    }

    /**
     * 创建并返回一个启用了Token记录的新实例
     */
    public PromptLoggingAdvisor enableTokenInfo() {
        return new PromptLoggingAdvisor(
                this.logTemplate, this.dateFormatter, this.enableLogging, true
        );
    }
}