package com.casy.casyaiagent.rag.component;


import com.casy.casyaiagent.service.BaiduTranslationService;
import org.jetbrains.annotations.NotNull;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * 自定义翻译型QueryTransformer（替代Spring AI内置大模型翻译）
 * @author Administrator
 */
@Component
public class BaiduTranslationQueryTransformer implements QueryTransformer {

    private final BaiduTranslationService baiduTranslationService;

    // 注入第三方翻译服务
    public BaiduTranslationQueryTransformer(BaiduTranslationService baiduTranslationService) {
        this.baiduTranslationService = baiduTranslationService;
    }

    /**
     * 核心转换方法：将用户中文查询翻译为英文（适配英文向量库）
     * @param originalQuery 原始用户查询
     * @return 转换后的查询（翻译后）
     */
    @NotNull
    @Override
    public Query transform(@NotNull Query originalQuery) {
        Assert.notNull(originalQuery, "query cannot be null");
        // 1. 获取原始查询文本
        String originalText = originalQuery.text();
        // 2. 调用第三方翻译API（示例：中译英，适配英文向量库检索）
        String translatedText = baiduTranslationService.translate("en", "zh", originalText);
        // 3. 构建新的Query对象
        return new Query(translatedText);
    }
}