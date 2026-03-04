package com.casy.casyaiagent.ai;

import ch.qos.logback.classic.Logger;
import com.casy.casyaiagent.advisor.PromptLoggingAdvisor;
import com.casy.casyaiagent.rag.LoveAppPromptTemplate;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

/**
 * @author Administrator
 */
@Component
public class LoveApp {

    private final ChatClient chatClient;

    private final MessageChatMemoryAdvisor chatMemoryAdvisor;

    private final VectorStore loveAppVectorStore;

    private final VectorStore pgVectorVectorStore;

    private final LoveAppPromptTemplate loveAppPromptTemplate;

    // 云知识库
    private final Advisor loveAppRagCloudAdvisor;

    private static final Logger log = (Logger) LoggerFactory.getLogger(LoveApp.class);

//    ChatMemory chatMemory = MessageWindowChatMemory.builder().build();

    private static final String SYSTEM_PROMPT = "扮演深耕恋爱心理领域的专家。开场向用户表明身份，告知用户可倾诉恋爱难题。" +
            "围绕单身、恋爱、已婚三种状态提问：单身状态询问社交圈拓展及追求心仪对象的困扰；" +
            "恋爱状态询问沟通、习惯差异引发的矛盾；已婚状态询问家庭责任与亲属关系处理的问题。" +
            "引导用户详述事情经过、对方反应及自身想法，以便给出专属解决方案。";

    public LoveApp(ChatModel dashscopChatModel, MessageChatMemoryAdvisor chatMemoryAdvisor, VectorStore loveAppVectorStore, VectorStore pgVectorVectorStore, LoveAppPromptTemplate loveAppPromptTemplate, Advisor loveAppRagCloudAdvisor) {
        this.loveAppPromptTemplate = loveAppPromptTemplate;
        this.chatMemoryAdvisor = chatMemoryAdvisor;
        this.loveAppVectorStore = loveAppVectorStore;
        this.loveAppRagCloudAdvisor = loveAppRagCloudAdvisor;
        this.pgVectorVectorStore = pgVectorVectorStore;
        chatClient = ChatClient.builder(dashscopChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        chatMemoryAdvisor,//对话记忆
                        new PromptLoggingAdvisor() //自定义日志
//                        new ReReadingAdvisor() //重读Advisor，提高ai的准确性，但增加token的消耗
                ).build();
    }

    public String doChat(String message, String chatId) {
        ChatResponse response = chatClient
                .prompt()
                .user(message)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, chatId))
                .call()
                .chatResponse();
        String content = response.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }

    public String doChatWithRag(String message, String chatId) {
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, chatId))
                // .advisors(loveAppPromptTemplate.getAdvisor(loveAppVectorStore)) // 自定义对话模板，告诉AI必须使用检索出来的内容进行回答
                // 应用知识库问答, 本地的rag增强
                .advisors(QuestionAnswerAdvisor.builder(loveAppVectorStore).build())
                // 应用增强检索服务（云知识库）
                // .advisors(loveAppRagCloudAdvisor)
                // 应用增强检索服务（pgVector）
                // .advisors(QuestionAnswerAdvisor.builder(pgVectorVectorStore).build())
                .advisors()
                .call()
                .chatResponse();
        return chatResponse.getResult().getOutput().getText();
    }

}
