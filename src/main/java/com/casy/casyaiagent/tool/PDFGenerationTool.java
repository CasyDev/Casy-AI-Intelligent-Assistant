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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.regex.Pattern;

/**
 * PDF 生成工具类
 * <p>
 * 支持两种模式：
 * 1. 纯文本模式：简单文本内容生成 PDF
 * 2. HTML 模式：根据 HTML 内容生成富文本 PDF（标题、列表、表格）
 */
public class PDFGenerationTool {

    private static final Pattern DARK_BG = Pattern.compile(
            "(?i)background(-color)?\\s*:\\s*(#[0-9a-f]{3,8}|rgb\\([^)]*\\)|rgba\\([^)]*\\)|black|navy|#1[0-9a-f]{5}|#2[0-9a-f]{5})\\s*;?");

    private static final String PRINT_CSS = """
            @page { margin: 16mm 18mm; }
            * { box-sizing: border-box; }
            html, body {
                font-family: "STSongStd-Light", "SimSun", "Microsoft YaHei", serif;
                font-size: 11pt;
                line-height: 1.65;
                color: #222 !important;
                background: #fff !important;
            }
            h1, h2, h3, h4, h5, h6 {
                font-weight: bold;
                margin: 16px 0 8px;
                page-break-after: avoid;
            }
            h1 { font-size: 18pt; color: #1a365d; }
            h2 { font-size: 14pt; color: #2b6cb0; }
            h3 { font-size: 12.5pt; color: #2f855a; }
            h4 { font-size: 11.5pt; color: #2d3748; }
            p { margin: 8px 0; }
            table {
                width: 100%;
                border-collapse: collapse;
                margin: 12px 0;
                font-size: 10pt;
            }
            th, td {
                border: 1px solid #cbd5e0;
                padding: 6px 8px;
                text-align: left;
            }
            th { background: #edf2f7; font-weight: bold; }
            ul, ol { margin: 8px 0; padding-left: 22px; }
            li { margin: 3px 0; }
            blockquote {
                margin: 10px 0;
                padding: 6px 12px;
                border-left: 3px solid #2b6cb0;
                color: #4a5568;
                background: #f7fafc;
            }
            pre, code, kbd, samp {
                font-family: Consolas, "Courier New", monospace;
                background: #f7fafc !important;
                color: #1a202c !important;
                border: 1px solid #e2e8f0;
            }
            code, kbd, samp { padding: 1px 4px; font-size: 9.5pt; }
            pre {
                padding: 10px 12px;
                border-radius: 4px;
                white-space: pre-wrap;
                word-wrap: break-word;
                overflow: hidden;
                font-size: 9pt;
                margin: 10px 0;
            }
            pre code { border: none; padding: 0; background: transparent !important; }
            img, svg, canvas, video, iframe { display: none !important; }
            """;

    @Tool(description = """
            生成 PDF 文件并返回下载标记。用户要求 PDF、报告、摘要文档时必须调用本工具。
            不要用 writeFile 把内容存成 HTML/TXT 代替 PDF。
            content 用简洁 HTML（h1-h3、p、ul/ol、table），不要图片、SVG、图表或深色背景。
            若同名文件被占用，工具会自动换文件名，请直接再调用本工具而不是改存 HTML。
            """)
    public String generatePDF(
            @ToolParam(description = "保存生成的 PDF 文件名称，如：report.pdf") String fileName,
            @ToolParam(description = "PDF 内容。HTML 请用 h1-h3、p、ul/ol、table，不要放 img/svg/图表") String content,
            @ToolParam(description = "内容是否为 HTML 格式，true=HTML，false=纯文本", required = false) Boolean isHtml) {

        boolean htmlMode = Boolean.TRUE.equals(isHtml) || looksLikeHtml(content);
        String safeName = sanitizeFileName(fileName);
        String fileDir = FileConstant.getFileSaveDir() + "/pdf";

        try {
            FileUtil.mkdir(fileDir);
            Path filePath = resolveWritablePdfPath(fileDir, safeName);
            safeName = filePath.getFileName().toString();

            if (htmlMode) {
                generateHtmlPdf(content, filePath.toString());
            } else {
                generateTextPdf(content, filePath.toString());
            }

            return "PDF 已生成：" + safeName + "\n[DOWNLOAD:pdf/" + safeName + "]";
        } catch (Exception e) {
            return "Error generating PDF: " + e.getMessage() + "。请换一个文件名后再次调用 generatePDF，不要改用 writeFile 保存 HTML。";
        }
    }

    private Path resolveWritablePdfPath(String fileDir, String safeName) {
        Path dir = Path.of(fileDir);
        Path target = dir.resolve(safeName);
        if (isWritable(target)) {
            return target;
        }
        String base = safeName.toLowerCase().endsWith(".pdf")
                ? safeName.substring(0, safeName.length() - 4)
                : safeName;
        return dir.resolve(base + "_" + System.currentTimeMillis() + ".pdf");
    }

    private boolean isWritable(Path path) {
        try {
            Files.createDirectories(path.getParent());
            if (!Files.exists(path)) {
                return true;
            }
            try (FileChannel ignored = FileChannel.open(path, StandardOpenOption.WRITE)) {
                return true;
            }
        } catch (IOException e) {
            return false;
        }
    }

    private String sanitizeFileName(String fileName) {
        String name = Paths.get(fileName == null ? "document.pdf" : fileName).getFileName().toString().trim();
        if (name.isBlank()) {
            name = "document.pdf";
        }
        name = name.replaceAll("[\\\\/:*?\"<>|]", "_");
        if (!name.toLowerCase().endsWith(".pdf")) {
            name = name + ".pdf";
        }
        return name;
    }

    private boolean looksLikeHtml(String content) {
        if (content == null) {
            return false;
        }
        String trimmed = content.trim().toLowerCase();
        return trimmed.startsWith("<!doctype") || trimmed.startsWith("<html")
                || trimmed.contains("<h1") || trimmed.contains("<h2") || trimmed.contains("<p>");
    }

    private void generateTextPdf(String content, String filePath) throws IOException {
        try (PdfWriter writer = new PdfWriter(filePath);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            PdfFont font = PdfFontFactory.createFont("STSongStd-Light", "UniGB-UCS2-H");
            document.setFont(font);
            document.add(new Paragraph(content));
        }
    }

    private void generateHtmlPdf(String htmlContent, String filePath) throws IOException {
        String fullHtml = prepareHtml(htmlContent);

        ConverterProperties converterProperties = new ConverterProperties();
        converterProperties.setBaseUri(Paths.get(FileConstant.getFileSaveDir()).toUri().toString());

        FontProvider fontProvider = new FontProvider();
        fontProvider.addStandardPdfFonts();
        try {
            fontProvider.addFont("STSongStd-Light", "UniGB-UCS2-H");
        } catch (Exception ignored) {
        }
        converterProperties.setFontProvider(fontProvider);

        try (PdfWriter writer = new PdfWriter(filePath);
             PdfDocument pdf = new PdfDocument(writer)) {
            pdf.setDefaultPageSize(PageSize.A4);
            HtmlConverter.convertToPdf(
                    new ByteArrayInputStream(fullHtml.getBytes(StandardCharsets.UTF_8)),
                    pdf,
                    converterProperties
            );
        }
    }

    /**
     * 清洗无法稳定转 PDF 的元素，并注入打印样式，避免深色占位块变成黑条。
     */
    String prepareHtml(String htmlContent) {
        org.jsoup.nodes.Document doc = Jsoup.parse(htmlContent == null ? "" : htmlContent);
        doc.outputSettings().syntax(org.jsoup.nodes.Document.OutputSettings.Syntax.xml);

        doc.select("img, svg, canvas, iframe, video, object, embed, picture, source").remove();
        doc.select("script, style").remove();

        for (Element el : doc.select("[style]")) {
            String cleaned = DARK_BG.matcher(el.attr("style")).replaceAll("");
            if (cleaned.isBlank()) {
                el.removeAttr("style");
            } else {
                el.attr("style", cleaned);
            }
        }

        // 去掉没有文字的装饰块（骨架屏、假图表、空 pre）
        for (Element el : doc.select("div, section, figure, aside, pre, span")) {
            if (el.text().isBlank() && el.select("table, img, li").isEmpty()) {
                el.remove();
            }
        }

        if (doc.head() == null) {
            doc.prependElement("head");
        }
        doc.head().appendElement("meta").attr("charset", "UTF-8");
        doc.head().appendElement("style").appendText(PRINT_CSS);
        return doc.outerHtml();
    }
}
