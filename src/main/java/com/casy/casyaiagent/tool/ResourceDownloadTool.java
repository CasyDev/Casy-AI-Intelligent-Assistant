package com.casy.casyaiagent.tool;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;
import com.casy.casyaiagent.constant.FileConstant;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.File;

/**
 * 资源下载工具
 * @author linlin
 */
public class ResourceDownloadTool {

    @Tool(description = "从给定的URL下载资源")
    public String downloadResource(@ToolParam(description = "资源下载链接") String url, @ToolParam(description = "保存下载资源的文件名称") String fileName) {
        String fileDir = FileConstant.FILE_SAVE_DIR + "/download";
        String filePath = fileDir + "/" + fileName;
        try {
            // 创建目录
            FileUtil.mkdir(fileDir);
            // 使用 Hutool 的 downloadFile 方法下载资源
            HttpUtil.downloadFile(url, new File(filePath));
            return "Resource downloaded successfully to: " + filePath;
        } catch (Exception e) {
            System.out.println(e);
            return "Error downloading resource: " + e.getMessage();
        }
    }

}
