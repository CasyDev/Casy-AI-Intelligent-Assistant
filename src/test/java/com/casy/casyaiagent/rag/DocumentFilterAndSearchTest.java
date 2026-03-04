package com.casy.casyaiagent.rag;

import com.casy.casyaiagent.ai.LoveApp;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.TranslationQueryTransformer;
import org.springframework.boot.test.context.SpringBootTest;

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
    void queryTransFormerTest () {
        Query query = new Query("啥是程序员鱼皮啊啊啊啊？");
        QueryTransformer queryTransformer = RewriteQueryTransformer.builder()
                .chatClientBuilder(ChatClient.builder(dashscopChatModel))
                .build();
        Query transformedQuery = queryTransformer.transform(query);
        System.out.println(transformedQuery);
    }

    /**
     * 查询翻译器
     */
    @Test
    void translationQueryTransFormerTest () {
        Query query = new Query("hi, who is coder yupi? please answer me");
        QueryTransformer queryTransformer = TranslationQueryTransformer.builder()
                .chatClientBuilder(ChatClient.builder(dashscopChatModel))
                .targetLanguage("zh")
                .build();
        Query transformedQuery = queryTransformer.transform(query);
        System.out.println(transformedQuery);
    }
}
