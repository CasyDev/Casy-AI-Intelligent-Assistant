package com.casy.casyaiagent.ai;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.casy.casyaiagent.advisor.PromptLoggingAdvisor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

/**
 * 企业智能客服：基于本地 Markdown 知识库的 RAG 对话
 */
@Slf4j
@Component
public class CustomerServiceApp {

    private static final String SYSTEM_PROMPT = """
            你是星河云（CASY Cloud）的企业智能客服，名字叫小河。页面已经展示身份，不要自我介绍，不要每轮都以「您好，我是小河」开头，直接回答即可。
            
            说话像靠谱的同事：自然、简洁、有温度，少用公文腔和套话。能一两句说清就不要写成通知。
            这是多轮对话：前面的用户消息和你的回复就是完整历史。用户问刚才问了什么、让你复述、用「这个/那个」指代时，必须根据历史回答，不要说没有对话记录。
            
            产品、套餐、价格、SLA、退款、账号安全等事实以检索到的知识库为准，不要编造数字或政策。知识库没有的，坦诚说不确定，并建议提交工单或拨打 400-888-0123。
            明显与星河云服务无关、也和当前对话无关的问题，再礼貌地拉回到能帮上忙的范围。
            """;

    /**
     * 覆盖 Spring AI 默认 RAG 模板（“no prior knowledge / 不在 context 就说不知道”），
     * 否则追问、回忆上一轮会被检索片段盖掉，看起来像对话记忆没生效。
     */
    private static final PromptTemplate RAG_PROMPT_TEMPLATE = new PromptTemplate("""
            下面是知识库检索到的参考资料，可能和当前问题不完全相关：
            
            ---------------------
            {context}
            ---------------------
            
            当前用户问题：{query}
            
            回答要求：
            1. 这是多轮对话，提示词里更早的消息就是对话历史。问「我的第一个问题是什么」「刚才说了啥」时，根据历史直接回答。
            2. 价格、套餐、账号、售后等事实优先用参考资料；资料明显无关就不要硬套。
            3. 不要自我介绍，不要说自己没有对话历史。
            """);

    private final ChatClient chatClient;

    public CustomerServiceApp(ChatModel dashscopChatModel,
                              MessageChatMemoryAdvisor chatMemoryAdvisor,
                              @Qualifier("customerServiceVectorStore") VectorStore customerServiceVectorStore,
                              @Value("${spring.ai.dashscope.chat.options.multi-model:false}") boolean multiModel) {
        Advisor ragAdvisor = RetrievalAugmentationAdvisor.builder()
                .documentRetriever(VectorStoreDocumentRetriever.builder()
                        .vectorStore(customerServiceVectorStore)
                        .similarityThreshold(0.55)
                        .topK(4)
                        .build())
                .queryAugmenter(ContextualQueryAugmenter.builder()
                        .allowEmptyContext(true)
                        .promptTemplate(RAG_PROMPT_TEMPLATE)
                        .build())
                .build();

        ChatClient.Builder chatClientBuilder = ChatClient.builder(dashscopChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        chatMemoryAdvisor,
                        ragAdvisor,
                        new PromptLoggingAdvisor()
                );
        if (multiModel) {
            chatClientBuilder.defaultOptions(DashScopeChatOptions.builder().multiModel(true).build());
        }
        this.chatClient = chatClientBuilder.build();
    }

    public Flux<String> doChatByStream(String message, String chatId) {
        return chatClient.prompt()
                .user(message)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, chatId))
                .stream()
                .content();
    }
}
