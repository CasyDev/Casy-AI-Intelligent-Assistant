package com.casy.casyaiagent.rag;

import com.casy.casyaiagent.CasyAiAgentApplication;
import com.casy.casyaiagent.advisor.PromptLoggingAdvisor;
import com.casy.casyaiagent.constant.Global;
import com.casy.casyaiagent.util.GlobalSpringContextInitializer;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.expansion.MultiQueryExpander;
import org.springframework.ai.rag.preretrieval.query.transformation.CompressionQueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.TranslationQueryTransformer;
import org.springframework.ai.rag.retrieval.join.ConcatenationDocumentJoiner;
import org.springframework.ai.rag.retrieval.join.DocumentJoiner;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2026/3/4 21:00
 */
@SpringBootTest
public class DocumentFilterAndSearchTest {

    @Resource
    ChatModel dashscopChatModel;

    /**
     * 查询转换（查询重写），使其更加清晰和详细
     */
    @Test
    void queryTransFormerTest() {
        Query query = new Query("啥是程序员鱼皮啊啊啊啊？");
        ChatClient.Builder chatClientBuilder = ChatClient.builder(dashscopChatModel).defaultAdvisors(new PromptLoggingAdvisor());
        QueryTransformer queryTransformer = RewriteQueryTransformer.builder()
                .chatClientBuilder(chatClientBuilder)
                .build();
        Query transformedQuery = queryTransformer.transform(query);
        System.out.println(transformedQuery);
    }

    /**
     * 查询翻译器
     */
    @Test
    void translationQueryTransFormerTest() {
        ChatClient.Builder chatClientBuilder = ChatClient.builder(dashscopChatModel).defaultAdvisors(new PromptLoggingAdvisor());
        Query query = new Query("hi, who is coder yupi? please answer me");
        QueryTransformer queryTransformer = TranslationQueryTransformer.builder()
                .chatClientBuilder(chatClientBuilder)
                .targetLanguage("zh")
                .build();
        Query transformedQuery = queryTransformer.transform(query);
        System.out.println(transformedQuery);
    }

    /**
     * 历史对话压缩成一个独立的查询，类似于概括总结
     */
    @Test
    void compressionQueryTransFormer() {
        Query query = Query.builder()
                .text("编程导航有啥内容？")
                .history(new UserMessage("谁是程序员鱼皮？"),
                        new AssistantMessage("编程导航的创始人 codefather.cn"))
                .build();
        ChatClient.Builder chatClientBuilder = ChatClient.builder(dashscopChatModel).defaultAdvisors(new PromptLoggingAdvisor());
        QueryTransformer queryTransformer = CompressionQueryTransformer.builder()
                .chatClientBuilder(chatClientBuilder)
                .build();
        Query transformedQuery = queryTransformer.transform(query);
        System.out.println(transformedQuery);
    }

    /**
     * 多查询扩展，使用大模型将一个查询扩展多个语义的不同变体
     *
     * 多查询扩展的完整使用流程可以包括三个步骤:
     * 1.使用扩展后的查询召回文档:遍历扩展后的查询列表，对每个查询使用 DocumentRetriever 来召回相关文档。
     * 2.整合召回的文档:将每个查询召回的文档进行整合，形成一个包含所有相关信息的文档集合。(也可以使用文档合并器去重）
     * 3.使用召回的文档改写Prompt:将整合后的文档内容添加到原始Prompt中，为大语言模型提供更丰富的上下文信息。
     * 需要注意，多查询扩展会增加查询次数和计算成本，效果也不易量化评估，所以个人建议慎用这种优化方式
     */

    @Test
    void mutiQueryExpanderTest() {
        ChatClient.Builder chatClientBuilder = ChatClient.builder(dashscopChatModel).defaultAdvisors(new PromptLoggingAdvisor());
        MultiQueryExpander queryExpander = MultiQueryExpander.builder()
                .chatClientBuilder(chatClientBuilder)
                .numberOfQueries(3)
                .build();
        Query originalQuery = new Query("单身久了如何找到合适的伴侣？");
        List<Query> queries = queryExpander.expand(originalQuery);
        System.out.println(queries);
        DocumentRetriever documentRetriever = VectorStoreDocumentRetriever.builder()
                .vectorStore(Global.getBean("loveAppVectorStore", VectorStore.class))
//                .similarityThreshold(0.73)
//                .topK(5)
//                .filterExpression(new FilterExpressionBuilder()
//                        .eq("genre", "fairytale")
//                        .build())
                .build();


        // 直接用扩展后的查询来获取文档
        Map<Query, List<List<Document>>> documentsMap = queries.stream().collect(
                Collectors.toMap(
                        // 第一个参数：Map的Key → 直接使用stream中的当前Query元素
                        query -> query,
                        // 第二个参数：Map的Value → 调用retrieve方法获取该Query对应的文档列表,并将List<Document>包装成List<List<Document>>
                        query -> List.of(documentRetriever.retrieve(query)),
                        // 第三个参数（可选但推荐）：解决重复Key的冲突策略
                        // 若queries中有重复的Query，保留已有值（也可根据业务选replacement）
                        (existingValue, newValue) -> existingValue
                ));
        DocumentJoiner documentJoiner = new ConcatenationDocumentJoiner();
        List<Document> documents = documentJoiner.join(documentsMap);

        // 构建带上下文的提示
        String context = documents.stream()
                .map(Document::getText) // 根据实际 Document 类调整
                .collect(Collectors.joining("\n---\n"));

        // 构造大模型 Prompt（原始问题 + 文档上下文）
        // Prompt 模板：明确告知大模型基于上下文回答问题
        String promptTemplate = """
                请基于以下上下文信息，回答我的问题。如果上下文没有相关信息，直接说"无法回答"。
                上下文：{context}
                问题：{question}
                """;
        PromptTemplate template = new PromptTemplate(promptTemplate);
        Prompt prompt = template.create(
                Map.of("context", context, "question", originalQuery.text()) // 替换占位符
        );

        // 调用大模型获取最终答案
        ChatClient chatClient = chatClientBuilder.build(); // 复用 builder
        String finalAnswer = chatClient.prompt()
                .user(prompt.getContents())
                .call()
                .content(); // 具体方法名取决于所用 SDK，如 Spring AI 的 content()
        System.out.println("最终答案：" + finalAnswer);
    }

    /**
     * ConcatenationDocumentJoiner文档合并器解析
     */
    void aa(Map<Query, List<List<Document>>> documentsForQuery) {
        new ArrayList<>( // 最终包装成 ArrayList 返回（因为 toList() 返不可变列表）
                documentsForQuery.values() // 步骤1：获取Map中所有的“二维List<Document>”（即所有value）
                        .stream() // 步骤2：将Collection<List<List<Document>>>转为 Stream<List<List<Document>>>
                        .flatMap(List::stream) // 步骤3：第一次扁平化：拆外层List → Stream<List<Document>>
                        .flatMap(List::stream) // 步骤4：第二次扁平化：拆内层List → Stream<Document>（二维转一维）
                        .collect(Collectors.toMap( // 步骤5：收集为Map<DocumentId, Document>，实现按id去重
                                Document::getId, // Map的key：Document的id（去重的依据）
                                Function.identity(), // Map的value：Document本身（Function.identity() 等价于 d -> d）
                                (existing, duplicate) -> existing // 冲突解决：重复id时保留已存在的（第一个），丢弃重复的
                        ))
                        .values() // 步骤6：获取去重后的Map的value → Collection<Document>（已去重的一维文档集合）
                        .stream() // 步骤7：转为 Stream<Document>，准备排序
                        .sorted( // 步骤8：按score降序排序
                                Comparator.comparingDouble( // 按double类型的score排序
                                        (Document doc) -> doc.getScore() != null ? doc.getScore() : 0.0 // 处理null：score为null时按0.0算
                                ).reversed() // 反转排序规则：从升序→降序（score高的排前面）
                        )
                        .toList() // 步骤9：排序后转为List<Document>（Java 16+ 特性，返回不可变List）
        );
    }
}
