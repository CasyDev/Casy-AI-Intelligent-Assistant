package com.casy.casyaiagent.rag.component;

import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Component
public class LoveAppDocumentLoader {

    private final ResourcePatternResolver resourcePatternResolver;

    LoveAppDocumentLoader(ResourcePatternResolver resourcePatternResolver) {
        this.resourcePatternResolver = resourcePatternResolver;
    }

    /**
     * 可以通过withAdditionalMetadata来添加元信息，也可以直接添加如下
     * documents.add(new Document(
     * "案例编号：LR-2023-001\n" +
     * "项目概述：180平米大平层现代简约风格客厅改造\n" +
     * "设计要点：\n" +
     * "1. 采用5.2米挑高的落地窗，最大化自然采光\n" +
     * "2. 主色调：云雾白(哑光，NCS S0500-N)配合莫兰迪灰\n" +
     * "3. 家具选择：意大利B&B品牌真皮沙发，北欧白橡木茶几\n" +
     * "空间效果：通透大气，适合商务接待和家庭日常起居",
     * Map.of(
     * "type", "interior",    // 文档类型
     * "year", "2025",        // 年份
     * "month", "05",         // 月份
     * "style", "modern",      // 装修风格
     * )));
     */

    public List<Document> loadMarkdownDocuments() {
        List<Document> documents = new ArrayList<>();
        try {
            Resource[] resources = resourcePatternResolver.getResources("classpath:document/*.md");
            for (Resource resource : resources) {
                String filename = resource.getFilename();
                // 取文件名中的婚姻状态信息
                String status = !filename.contains("篇") ? "分类推荐" : filename.substring(filename.indexOf("篇") - 2, filename.indexOf("篇"));
                MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                        .withHorizontalRuleCreateDocument(true) // Markdown 中的水平规则将创建新的 Document 对象
                        .withIncludeCodeBlock(false) // 不为代码块创建新的document对象
                        .withIncludeBlockquote(false) // 不为引用块创建新的document对象
                        .withAdditionalMetadata("filename", filename) // 附加元数据
                        .withAdditionalMetadata("status", status)
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
