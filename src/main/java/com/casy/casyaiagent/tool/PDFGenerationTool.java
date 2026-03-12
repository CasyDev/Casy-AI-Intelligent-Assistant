package com.casy.casyaiagent.tool;

import cn.hutool.core.io.FileUtil;
import com.casy.casyaiagent.constant.FileConstant;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.IOException;

/**
 * 生成包含指定内容的PDF文件
 * @author linlin
 */
public class PDFGenerationTool {

    /**
     * 有时候，工具执行的结果不需要再经过 AI 模型处理，而是希望直接返回给用户（比如生成 PDF 文档）。Spring AI 通过 returnDirect 属性支持这一功能
     * 立即返回模式改变了工具调用的基本流程：
     *
     * 定义工具时，将 returnDirect 属性设为 true
     * 当模型请求调用这个工具时，应用程序执行工具并获取结果
     * 结果直接返回给调用者，不再 发送回模型进行进一步处理
     * 这种模式很适合‍‍需要返回二进制数据（比如图片 / 文件）的工具、返回大量数据而不需要 AI 解释的工具，以及产生明确结果的操作（如数据库操作）。
     */
    @Tool(description = "生成包含指定内容的PDF文件",  returnDirect = true)
    public String generatePDF(
            @ToolParam(description = "保存生成的PDF文件名称") String fileName,
            @ToolParam(description = "PDF中将包含的内容") String content) {
        String fileDir = FileConstant.FILE_SAVE_DIR + "/pdf";
        String filePath = fileDir + "/" + fileName;
        try {
            // 创建目录
            FileUtil.mkdir(fileDir);
            // 创建 PdfWriter 和 PdfDocument 对象
            try (PdfWriter writer = new PdfWriter(filePath);
                 PdfDocument pdf = new PdfDocument(writer);
                 Document document = new Document(pdf)) {
                // 自定义字体（需要人工下载字体文件到特定目录）
//                String fontPath = Paths.get("src/main/resources/static/fonts/simsun.ttf")
//                        .toAbsolutePath().toString();
//                PdfFont font = PdfFontFactory.createFont(fontPath,
//                        PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
                // 使用内置中文字体
                PdfFont font = PdfFontFactory.createFont("STSongStd-Light", "UniGB-UCS2-H");
                document.setFont(font);
                // 创建段落
                Paragraph paragraph = new Paragraph(content);
                // 添加段落并关闭文档
                document.add(paragraph);
            }
            return "PDF generated successfully to: " + filePath;
        } catch (IOException e) {
            return "Error generating PDF: " + e.getMessage();
        }
    }
}
