package com.casy.casyaiagent;

import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.template.st.StTemplateRenderer;

import java.util.Map;

/**
 * @author linlin
 * @version 1.0
 * @description: 结构化输出测试类
 * @date 2025/12/24 20:08
 */
public class PromptTemplateTest {
    public static void main(String[] args) {
        PromptTemplate promptTemplate = PromptTemplate.builder()
                .renderer(StTemplateRenderer.builder().startDelimiterToken('+').endDelimiterToken('>').build())
                .template("""
            Tell me the names of 5 movies whose soundtrack was composed by +composer>.
            """)
                .build();

        String prompt = promptTemplate.render(Map.of("composer", "John Williams"));
        System.out.println(prompt);
    }
}
