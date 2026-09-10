package com.casy.casyaiagent.tool;

import cn.hutool.core.io.FileUtil;
import com.casy.casyaiagent.constant.FileConstant;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * 文件操作工具
 * @author linlin
 */
public class FileOperationTool {

    private String fileDir() {
        return FileConstant.getFileSaveDir() + "/file";
    }

    @Tool(description = "读取文件中的内容")
    public String readFile(@ToolParam(description = "要读取的文件名称") String fileName) {
        String filePath = fileDir() + "/" + fileName;
        try {
            return FileUtil.readUtf8String(filePath);
        } catch (Exception e) {
            return "Error reading file: " + e.getMessage();
        }
    }

    @Tool(description = "写入普通文本文件（如 .txt/.md/.json）。禁止用来生成 PDF，也禁止把 PDF 内容存成 HTML。用户要 PDF 必须调用 generatePDF。")
    public String writeFile(
        @ToolParam(description = "要写入的文件名称") String fileName,
        @ToolParam(description = "要写入文件的内容") String content) {
        String name = fileName == null ? "" : fileName.toLowerCase();
        if (name.endsWith(".pdf") || name.endsWith(".html") || name.endsWith(".htm")) {
            return "已拒绝：writeFile 不能生成 PDF，也不能用 HTML 代替 PDF。请立即调用 generatePDF（fileName 以 .pdf 结尾，isHtml=true，content 用简洁 HTML）。";
        }
        String filePath = fileDir() + "/" + fileName;
        try {
            FileUtil.mkdir(fileDir());
            FileUtil.writeUtf8String(content, filePath);
            return "File written successfully to: " + filePath;
        } catch (Exception e) {
            return "Error writing to file: " + e.getMessage();
        }
    }
}
