package com.casy.casyaiagent.rag.config;

import com.casy.casyaiagent.rag.component.LoveAppDocumentLoader;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.springframework.ai.vectorstore.pgvector.PgVectorStore.PgDistanceType.COSINE_DISTANCE;
import static org.springframework.ai.vectorstore.pgvector.PgVectorStore.PgIndexType.HNSW;

/**
 * 自定义PgVectorStore配置，由于付费暂不使用
 * @author linlin
 */
//@Configuration
public class PgVectorVectorStoreConfig {

    private final LoveAppDocumentLoader loveAppDocumentLoader;
    private final JdbcTemplate jdbcTemplate;
    private final EmbeddingModel dashscopeEmbeddingModel;
    public PgVectorVectorStoreConfig(LoveAppDocumentLoader loveAppDocumentLoader,JdbcTemplate jdbcTemplate,EmbeddingModel dashscopeEmbeddingModel) {
        this.loveAppDocumentLoader = loveAppDocumentLoader;
        this.jdbcTemplate = jdbcTemplate;
        this.dashscopeEmbeddingModel = dashscopeEmbeddingModel;
    }

    @Bean
    public VectorStore pgVectorVectorStore() {
        PgVectorStore vectorStore = PgVectorStore.builder(jdbcTemplate, dashscopeEmbeddingModel)
                .dimensions(1024)                    // 不要盲目设置，这个向量的维度
                .distanceType(COSINE_DISTANCE)       // 相似度计算方式：余弦距离
                .indexType(HNSW)                     // 索引类型：HNSW（高效近邻搜索）
                .initializeSchema(true)              // 自动初始化表结构（首次启动后可改为false）
                .schemaName("public")                // 数据库schema
                .vectorTableName("vector_store")     // 向量表名
                .maxDocumentBatchSize(10000)         // 最大批量大小
                .build();
        initDocuments(vectorStore);
        return vectorStore;
    }

    public void initDocuments(PgVectorStore vectorStore) {
        // 检查向量表是否已有数据
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM vector_store", Integer.class);
        if (count == 0) {
            // 无数据，执行文档加载和插入
            List<Document> documents = loveAppDocumentLoader.loadMarkdownDocuments();
            int batchSize = 10;
            for (int i = 0; i < documents.size(); i += batchSize) {
                int end = Math.min(i + batchSize, documents.size());
                vectorStore.add(documents.subList(i, end));
            }
            System.out.println("Initial documents loaded into vector store.");
        } else {
            System.out.println("Vector store already contains data, skipping initialization.");
        }
    }
}
