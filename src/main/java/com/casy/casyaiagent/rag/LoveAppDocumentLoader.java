package com.casy.casyaiagent.rag;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author linlin
 * @version 1.0
 * @description: markdown文档加载器
 * @date 2026/1/19 20:46
 */
@Component
public class LoveAppDocumentLoader {

    private static final Logger log = (Logger) LoggerFactory.getLogger(LoveAppDocumentLoader.class);

    private final ResourcePatternResolver resourcePatternResolver;

    LoveAppDocumentLoader(ResourcePatternResolver resourcePatternResolver) {
        this.resourcePatternResolver = resourcePatternResolver;
    }

    public List<Document> loadMarkdownDocuments() {
        List<Document> documents = new ArrayList<>();
        try {
            Resource[] resources = resourcePatternResolver.getResources("classpath:document/*.md");
            for (Resource resource : resources) {
                String filename = resource.getFilename();
                MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                        .withHorizontalRuleCreateDocument(true) // Markdown 中的水平规则将创建新的 Document 对象
                        .withIncludeCodeBlock(false) // 不为代码块创建新的document对象
                        .withIncludeBlockquote(false) // 不为引用块创建新的document对象
                        .withAdditionalMetadata("filename", filename) // 附加元数据
                        .build();
                MarkdownDocumentReader reader = new MarkdownDocumentReader(resource, config);
                documents.addAll(reader.get());
            }
        } catch (Exception e) {
            log.error("Failed to load documents", e);
        }
        return documents;
    }
}
