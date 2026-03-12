package com.casy.casyaiagent.ai;

import ch.qos.logback.classic.Logger;
import com.casy.casyaiagent.advisor.PromptLoggingAdvisor;
import com.casy.casyaiagent.constant.Global;
import com.casy.casyaiagent.rag.component.BaiduTranslationQueryTransformer;
import com.casy.casyaiagent.rag.component.LoveAppPromptTemplate;
import com.casy.casyaiagent.rag.component.QueryRewriter;
import com.casy.casyaiagent.rag.factory.LoveAppRagCustomAdvisorFactory;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.vectorstore.SearchRequest;
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

    private final ToolCallback[] allTools; //工具调用类

    private static final Logger log = (Logger) LoggerFactory.getLogger(LoveApp.class);

//    ChatMemory chatMemory = MessageWindowChatMemory.builder().build();

    private static final String SYSTEM_PROMPT = "扮演深耕恋爱心理领域的专家。开场向用户表明身份，告知用户可倾诉恋爱难题。" +
            "围绕单身、恋爱、已婚三种状态提问：单身状态询问社交圈拓展及追求心仪对象的困扰；" +
            "恋爱状态询问沟通、习惯差异引发的矛盾；已婚状态询问家庭责任与亲属关系处理的问题。" +
            "引导用户详述事情经过、对方反应及自身想法，以便给出专属解决方案。";

    public LoveApp(ChatModel dashscopChatModel, MessageChatMemoryAdvisor chatMemoryAdvisor,
                   VectorStore loveAppVectorStore, VectorStore pgVectorVectorStore,
                   LoveAppPromptTemplate loveAppPromptTemplate, Advisor loveAppRagCloudAdvisor,
                   ToolCallback[] allTools) {
        this.loveAppPromptTemplate = loveAppPromptTemplate;
        this.chatMemoryAdvisor = chatMemoryAdvisor;
        this.loveAppVectorStore = loveAppVectorStore;
        this.loveAppRagCloudAdvisor = loveAppRagCloudAdvisor;
        this.pgVectorVectorStore = pgVectorVectorStore;
        this.allTools = allTools;
        // var 是 Java 10 引入的局部变量类型推断关键字，核心作用是让编译器根据变量赋值语句的右侧表达式，自动推断出局部变量的具体类型，从而简化代码书写
        var qaAdvisor = QuestionAnswerAdvisor.builder(loveAppVectorStore)
                // 相似度阈值为 0.8，并返回最相关的前 6 个结果
                .searchRequest(SearchRequest.builder().similarityThreshold(0.8d).topK(6).build()) //相似阈值和前几个
                .build();

        // RetrievalAugmentationAdvisor是功能更强大的QuestionAnswerAdvisor支持高级的RAG流程比如结合查询转换器
        Advisor retrievalAugmentationAdvisor = RetrievalAugmentationAdvisor.builder()
                .queryTransformers(RewriteQueryTransformer.builder()
                        .chatClientBuilder(ChatClient.builder(dashscopChatModel).build().mutate())
                        .build())
                .documentRetriever(VectorStoreDocumentRetriever.builder()
                        .similarityThreshold(0.50)
                        .vectorStore(loveAppVectorStore)
                        .build())
                .queryAugmenter(ContextualQueryAugmenter.builder() // ContextualQueryAugmenter空上下文处理，为空给出友好提示
                        // .promptTemplate(customProptTemplate) // 可以自定义提示词模板
                        .allowEmptyContext(true) // 为true允许模型在没有找到相关文档的情况下也生成回答
                        .build())
                .build();


        chatClient = ChatClient.builder(dashscopChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        chatMemoryAdvisor,//对话记忆
                        new PromptLoggingAdvisor() //自定义日志
//                        new ReReadingAdvisor() //重读Advisor，提高ai的准确性，但增加token的消耗
//                        ,qaAdvisor // QuestionAnswerAdvisor的默认实现和自定义searchRequest以实现更灵活的查询
                )
                .build();
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
                // .advisors(a -> a.param(QuestionAnswerAdvisor.FILTER_EXPRESSION, "type == 'web'")) // 运行时添加查询过滤表达式
                .advisors()
                .call()
                .chatResponse();
        return chatResponse.getResult().getOutput().getText();
    }

    /**
     * 使用查询重写
     */
    public String doChatWithRagForQueryRewriter(String message, String chatId) {
        // 查询重写
        String rewrittenMessage = Global.getBean(QueryRewriter.class).doQueryRewrite(message);
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(rewrittenMessage)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, chatId))
                .call()
                .chatResponse();
        return chatResponse.getResult().getOutput().getText();
    }

    /**
     * 指定婚姻状态的类别过滤文档
     */
    public String doChatWithRagForQueryFilterExpression(String message, String chatId) {
        ChatResponse chatResponse = chatClient
                .prompt()
                .advisors(LoveAppRagCustomAdvisorFactory.createLoveAppRagCustomAdvisor(loveAppVectorStore, "已婚"))
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, chatId))
                .user(message)
                .call()
                .chatResponse();
        return chatResponse.getResult().getOutput().getText();
    }

    /**
     * 自定义空上下文处理
     */
    public String doChatWithRagForContextualQuery(String message, String chatId) {
        ChatResponse chatResponse = chatClient
                .prompt()
                .advisors(LoveAppRagCustomAdvisorFactory.createLoveAppRagCustomAdvisor(loveAppVectorStore, "单身"))// 问题并不属于单身问题
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, chatId))
                .user(message)
                .call()
                .chatResponse();
        return chatResponse.getResult().getOutput().getText();
    }

    /**
     * 自定义空上下文处理
     */
    public String doChatWithRagForBaiduTranslation(String message, String chatId, String type, boolean isTranslation) {
        if (isTranslation) {
            Query transform = Global.getBean(BaiduTranslationQueryTransformer.class).transform(new Query(message));
            message = transform.text();
        }
        ChatResponse chatResponse = chatClient
                .prompt()
                .advisors(LoveAppRagCustomAdvisorFactory.createLoveAppRagCustomAdvisor(loveAppVectorStore, type))// 问题并不属于单身问题
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, chatId))
                .user(message)
                .call()
                .chatResponse();
        return chatResponse.getResult().getOutput().getText();
    }

    // 使用工具调用
    public String doChatWithTools(String message, String chatId) {
        ChatResponse response = chatClient
                .prompt()
                .user(message)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, chatId))
                .toolCallbacks(allTools)
                // 可以传递上下文参数，比如说帮我查询用户信息，这就可以直接拿到用户名，它可以携带任何与当前请求相关的信息，但这些信息 不会传递给 AI 模型，只在应用程序内部使用
                // 用户认证信息：可以在上下文中传递用户 token，而不暴露给模型
                // 请求追踪：在上下文中添加请求 ID，便于日志追踪和调试
                // 自定义配置：根据不同场景传递特定配置参数
                // 举个应用例子，假如做了一个用户自助退款功能，如果已登录用户跟 AI 说：”我要退款“，AI 就不需要再问用户 “你是谁？”，让用户自己输入退款信息了；而是直接从系统中读取到 userId，在工具调用时根据 userId 操作退款即可。
                // .toolContext(Map.of("userName", "yupi"))
                .call()
                .chatResponse();
        String content = response.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }
}
