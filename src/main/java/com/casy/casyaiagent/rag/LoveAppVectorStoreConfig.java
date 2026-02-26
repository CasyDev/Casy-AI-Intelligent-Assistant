package com.casy.casyaiagent.rag;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author linlin
 * @version 1.0
 * @description: 初始化向量数据库，并且保存文档
 * @date 2026/1/19 21:04
 */
@Configuration
public class LoveAppVectorStoreConfig {
    private final LoveAppDocumentLoader loveAppDocumentLoader;

    public LoveAppVectorStoreConfig(LoveAppDocumentLoader loveAppDocumentLoader) {
        this.loveAppDocumentLoader = loveAppDocumentLoader;
    }

    /**
     * 基于内存的向量数据库
     */
    @Bean
    VectorStore loveAppVectorStore(EmbeddingModel dashscopeEmbeddingModel) {
        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(dashscopeEmbeddingModel).build();
        // 加载文档
        List<Document> documents = loveAppDocumentLoader.loadMarkdownDocuments();
        simpleVectorStore.add(documents);
        return simpleVectorStore;
    }
}
