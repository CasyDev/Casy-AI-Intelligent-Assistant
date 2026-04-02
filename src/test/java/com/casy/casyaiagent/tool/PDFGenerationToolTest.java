package com.casy.casyaiagent.tool;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PDFGenerationToolTest {

    @Test
    public void testGeneratePDF() {
        PDFGenerationTool tool = new PDFGenerationTool();
        String fileName = "测试PDF生成.pdf";
        String content = "测试PDF生成内容";
        String result = tool.generatePDF(fileName, content, false);
        assertNotNull(result);
    }
}
