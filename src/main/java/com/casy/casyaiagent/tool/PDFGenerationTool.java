package com.casy.casyaiagent.tool;

import cn.hutool.core.io.FileUtil;
import com.casy.casyaiagent.constant.FileConstant;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.element.Paragraph;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

/**
 * PDF 生成工具类
 * <p>
 * 支持两种模式：
 * 1. 纯文本模式：简单文本内容生成 PDF
 * 2. HTML 模式：根据 HTML 内容生成富文本 PDF（支持图片、表格、样式等）
 *
 * @author linlin
 */
public class PDFGenerationTool {

    /**
     * 生成 PDF 文件（支持 HTML 内容）
     * <p>
     * 当模型请求调用这个工具时，应用程序执行工具并获取结果
     * 结果直接返回给调用者，不再发送回模型进行进一步处理
     *
     * @param fileName 保存生成的 PDF 文件名称（如：report.pdf）
     * @param content  PDF 中将包含的内容（支持纯文本或 HTML 格式）
     * @param isHtml   内容是否为 HTML 格式，true 表示 HTML，false 表示纯文本
     * @return 生成结果信息
     */
    @Tool(description = "根据内容生成 PDF 文件，支持纯文本和 HTML 格式。HTML 格式支持图片、表格、样式等富文本内容", returnDirect = true)
    public String generatePDF(
            @ToolParam(description = "保存生成的 PDF 文件名称，如：report.pdf") String fileName,
            @ToolParam(description = "PDF 中将包含的内容，支持纯文本或 HTML 格式") String content,
            @ToolParam(description = "内容是否为 HTML 格式，true=HTML，false=纯文本", required = false) Boolean isHtml) {

        // 默认按纯文本处理
        boolean htmlMode = isHtml != null && isHtml;

        String fileDir = FileConstant.FILE_SAVE_DIR + "/pdf";
        String filePath = fileDir + "/" + fileName;

        try {
            // 创建目录
            FileUtil.mkdir(fileDir);

            if (htmlMode) {
                // HTML 模式：使用 html2pdf 转换
                generateHtmlPdf(content, filePath);
            } else {
                // 纯文本模式：简单段落
                generateTextPdf(content, filePath);
            }

            return "PDF generated successfully to: " + filePath;
        } catch (Exception e) {
            return "Error generating PDF: " + e.getMessage();
        }
    }

    /**
     * 生成纯文本 PDF
     */
    private void generateTextPdf(String content, String filePath) throws IOException {
        try (PdfWriter writer = new PdfWriter(filePath);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            // 使用支持中文的字体
            PdfFont font = PdfFontFactory.createFont("STSongStd-Light", "UniGB-UCS2-H");
            document.setFont(font);

            // 创建段落并添加
            Paragraph paragraph = new Paragraph(content);
            document.add(paragraph);
        }
    }

    /**
     * 生成 HTML PDF
     */
    private void generateHtmlPdf(String htmlContent, String filePath) throws IOException {
        // 确保 HTML 有基本的文档结构
        String fullHtml = wrapHtml(htmlContent);

        // 配置转换属性
        ConverterProperties converterProperties = new ConverterProperties();

        // 设置基础 URI，用于解析相对路径的图片等资源
        converterProperties.setBaseUri(Paths.get(FileConstant.FILE_SAVE_DIR).toUri().toString());

        // 配置字体提供者以支持中文
        FontProvider fontProvider = new FontProvider();
        fontProvider.addStandardPdfFonts();
        // 添加中文字体支持
        try {
            fontProvider.addFont("STSongStd-Light", "UniGB-UCS2-H");
        } catch (Exception e) {
            // 如果内置字体不可用，使用默认字体
        }
        converterProperties.setFontProvider(fontProvider);

        // 创建 PDF 写入器
        try (PdfWriter writer = new PdfWriter(filePath);
             PdfDocument pdf = new PdfDocument(writer)) {

            // 设置页面大小为 A4
            pdf.setDefaultPageSize(PageSize.A4);

            // 转换 HTML 到 PDF
            HtmlConverter.convertToPdf(
                    new ByteArrayInputStream(fullHtml.getBytes(StandardCharsets.UTF_8)),
                    pdf,
                    converterProperties
            );
        }
    }

    /**
     * 包装 HTML 内容，添加必要的文档结构
     */
    private String wrapHtml(String htmlContent) {
        // 如果内容已经是完整的 HTML 文档，直接返回
        if (htmlContent.trim().toLowerCase().startsWith("<!doctype") ||
                htmlContent.trim().toLowerCase().startsWith("<html")) {
            return htmlContent;
        }

        // 包装成完整的 HTML 文档
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body {
                            font-family: "STSongStd-Light", "SimSun", serif;
                            font-size: 12pt;
                            line-height: 1.6;
                            margin: 20px;
                        }
                        h1, h2, h3, h4, h5, h6 {
                            font-weight: bold;
                            margin-top: 20px;
                            margin-bottom: 10px;
                        }
                        h1 { font-size: 18pt; }
                        h2 { font-size: 16pt; }
                        h3 { font-size: 14pt; }
                        p { margin: 10px 0; }
                        table {
                            width: 100%%;
                            border-collapse: collapse;
                            margin: 15px 0;
                        }
                        th, td {
                            border: 1px solid #ddd;
                            padding: 8px;
                            text-align: left;
                        }
                        th {
                            background-color: #f2f2f2;
                            font-weight: bold;
                        }
                        img {
                            max-width: 100%%;
                            height: auto;
                        }
                        ul, ol {
                            margin: 10px 0;
                            padding-left: 30px;
                        }
                    </style>
                </head>
                <body>
                    %s
                </body>
                </html>
                """.formatted(htmlContent);
    }
}
