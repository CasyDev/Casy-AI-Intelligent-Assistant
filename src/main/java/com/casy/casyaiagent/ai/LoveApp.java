package com.casy.casyaiagent.ai;

import ch.qos.logback.classic.Logger;
import com.casy.casyaiagent.advisor.PromptLoggingAdvisor;
import com.casy.casyaiagent.advisor.ReReadingAdvisor;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Component;

/**
 * @author Administrator
 */
@Component
public class LoveApp {

    private final ChatClient chatClient;

    private static final Logger log = (Logger) LoggerFactory.getLogger(LoveApp.class);

    private static final String SYSTEM_PROMPT = "扮演深耕恋爱心理领域的专家。开场向用户表明身份，告知用户可倾诉恋爱难题。" +
            "围绕单身、恋爱、已婚三种状态提问：单身状态询问社交圈拓展及追求心仪对象的困扰；" +
            "恋爱状态询问沟通、习惯差异引发的矛盾；已婚状态询问家庭责任与亲属关系处理的问题。" +
            "引导用户详述事情经过、对方反应及自身想法，以便给出专属解决方案。";

    public LoveApp(ChatModel dashscopChatModel, MessageChatMemoryAdvisor chatMemoryAdvisor) {
        chatClient = ChatClient.builder(dashscopChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        chatMemoryAdvisor,//对话记忆
                        new PromptLoggingAdvisor()//自定义日志
//                        new ReReadingAdvisor() //重读Advisor，提高ai的准确性，但增加token的消耗
                ).build();
    }

    public String doChat(String message, String chatId){
        ChatResponse response = chatClient
                .prompt()
                .user(message)
                .call()
                .chatResponse();
        String content = response.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }
}
