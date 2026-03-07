package com.casy.casyaiagent.rag;

import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingModel;
import com.casy.casyaiagent.constant.Global;
import com.casy.casyaiagent.rag.component.MyTokenTextSplitter;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.List;

/**
 * @author linlin
 * @version 1.0
 * @description: 初始化向量数据库，并且保存文档到本地内存
 * @date 2026/1/19 21:04
 */
@Configuration
public class LoveAppLocalVectorStoreConfig {
    /**
     * 基于内存的向量数据库
     */
    @Bean
    @Lazy
    VectorStore loveAppVectorStore() {
        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(Global.getBean(DashScopeEmbeddingModel.class)).build();
        // 加载文档
        List<Document> documents = Global.getBean(LoveAppDocumentLoader.class).loadMarkdownDocuments();
        // 按token分切，语义不全不如不用
//        List<Document> splitedDocuments = Global.getBean(MyTokenTextSplitter.class).splitCustomized(documents);
//        simpleVectorStore.add(splitedDocuments);
        simpleVectorStore.add(documents);
        return simpleVectorStore;
    }
}
