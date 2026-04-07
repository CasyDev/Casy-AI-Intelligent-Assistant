package com.casy.casyaiagent.controller;


import com.casy.casyaiagent.agent.CasyManus;
import com.casy.casyaiagent.ai.LoveApp;
import com.casy.casyaiagent.constant.Global;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/ai")
public class AiController {

    // 使用 ObjectProvider 获取 Prototype 作用域的 CasyManus 实例
    // 这样每次调用 getIfAvailable() 都会创建新的实例
//    @Resource
//    private ObjectProvider<CasyManus> casyManusProvider;

    @GetMapping("/love_app/chat/sync")
    public String doChatWithLoveAppSync(String message, String chatId) {
        return Global.getBean(LoveApp.class).doChat(message, chatId);
    }

    /**
     * 返回‍ Flux 响应式对象，并且添加 SSE 对应的 MediaType
     * MediaType.TEXT_EVENT_STREAM_VALUE	等于 "text/event-stream"
     * 作用	服务器可以持续不断地向客户端推送数据
     * 连接方式	基于 HTTP，单向（服务器 → 客户端）
     * 典型场景	AI 流式回答、股票实时行情、消息通知
     *
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping(value = "/love_app/chat/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> doChatWithLoveAppSse(String message, String chatId) {
        return Global.getBean(LoveApp.class).doChatByStream(message, chatId);
    }

    /**
     * 返回 Flux 对象，并且设置泛型为 ServerSentEvent。使用这种方式可以省略 MediaType
     * ServerSentEvent 是 Spring 提供的 SSE 事件封装类，用于构造符合 SSE 协议格式的数据包
     *ServerSentEvent 可以设置什么？
     * ServerSentEvent.<String>builder()
     *     .data(chunk)              // 事件数据（必填）
     *     .id("msg-001")           // 事件 ID（用于断线续传）
     *     .event("ai-message")     // 事件类型（前端可以监听特定类型）
     *     .retry(3000)             // 断线后 3 秒重连
     *     .comment("调试信息")      // 注释（不会触发 onmessage）
     *     .build();
     * 对应 SSE 格式：
     *
     * id: msg-001
     * event: ai-message
     * data: chunk内容
     * retry: 3000
     * : 调试信息
     *
     * 一句话：把 String 包装成 SSE 标准格式，让浏览器能正确识别和处理流式数据
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping(value = "/love_app/chat/SSE")
    public Flux<ServerSentEvent<String>> doChatWithLoveAppSSE(String message, String chatId) {
        return Global.getBean(LoveApp.class).doChatByStream(message, chatId)
                .map(chunk -> ServerSentEvent.<String>builder().data(chunk).build());
    }

    /**
     * 使用 SSEEmiter，通过 send 方法持续向 SseEmitter 发送消息（有点像 IO 操作）
     * SseEmitter 是什么？
     * Spring 提供的 SSE 专用工具类，用于在 Servlet 环境 下实现服务器推送：
     *
     * 特性	说明
     * 适用框架	Spring MVC（Tomcat/Jetty）
     * 优点	兼容性好，支持传统 Servlet 环境
     * 缺点	代码较繁琐，需要手动管理订阅
     * 与 Flux<ServerSentEvent> 区别	后者是 WebFlux（Netty），代码更简洁
     *
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping("/love_app/chat/sse/emitter")
    public SseEmitter doChatWithLoveAppSseEmitter(String message, String chatId) {
        // 1. 创建 SseEmitter，设置 3 分钟超时
        SseEmitter sseEmitter = new SseEmitter(180000L);

        // ✅ 添加超时回调
        sseEmitter.onTimeout(() -> {
            log.warn("SSE 连接超时");
            sseEmitter.complete();
        });

        // ✅ 添加连接断开回调
        sseEmitter.onCompletion(() -> {
            log.info("SSE 连接关闭");
        });

        // 2. 获取 Flux 流并订阅
        Global.getBean(LoveApp.class).doChatByStream(message, chatId)
                .subscribe(
                        // onNext: 收到每条数据时
                        chunk -> {
                            try {
                                sseEmitter.send(chunk);
                            } catch(IOException e) {
                                sseEmitter.completeWithError(e);
                            }
                        },
                        // onError: 发生错误时
                        sseEmitter::completeWithError,
                        // onComplete: 流结束时
                        sseEmitter::complete
                );

        // 3. 立即返回 emitter，HTTP 连接保持打开
        return sseEmitter;
    }

    /**
     * 流式调用 Manus 超级智能体
     *
     * @param message
     * @return
     */
    @GetMapping("/manus/chat")
    public SseEmitter doChatWithManus(String message) {
        return Global.getBean(CasyManus.class).runStream(message);
    }
}
