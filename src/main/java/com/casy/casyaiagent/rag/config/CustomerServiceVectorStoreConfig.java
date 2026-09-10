package com.casy.casyaiagent.rag.config;

import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingModel;
import com.casy.casyaiagent.constant.Global;
import com.casy.casyaiagent.rag.component.CustomerServiceDocumentLoader;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.List;

/**
 * 企业智能客服本地向量库
 */
@Configuration
public class CustomerServiceVectorStoreConfig {

    @Bean
    @Lazy
    VectorStore customerServiceVectorStore() {
        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(Global.getBean(DashScopeEmbeddingModel.class)).build();
        List<Document> documents = Global.getBean(CustomerServiceDocumentLoader.class).loadMarkdownDocuments();
        simpleVectorStore.add(documents);
        return simpleVectorStore;
    }
}
