package com.casy.casyaiagent.tool;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 终端执行执行工具
 * @author linlin
 */
public class TerminalOperationTool {

    @Tool(description = "在终端中执行命令")
    public String executeTerminalCommand(@ToolParam(description = "要在终端执行的命令") String command) {
        StringBuilder output = new StringBuilder();
        StringBuilder errorOutput = new StringBuilder(); // 新增：存储错误输出
        Process process = null;
        try {
            ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", command);
            process = builder.start();
            // 1. 读取正常输出（指定GBK编码，独立线程避免阻塞）
            Process finalProcess = process;
            new Thread(() -> readStream(finalProcess.getInputStream(), output, "GBK")).start();
            // 2. 读取错误输出（关键：获取具体错误原因）
            Process finalProcess1 = process;
            new Thread(() -> readStream(finalProcess1.getErrorStream(), errorOutput, "GBK")).start();
            // 等待命令执行完成
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                output.append("执行失败,")
                        .append("Exit Code: ").append(exitCode)
                        .append("命令：").append(command)
                        .append("原因：").append(errorOutput);
            }
        } catch (IOException | InterruptedException e) {
            output.append("Error executing command: ").append(e.getMessage());
        } finally {
            if (process != null) {
                process.destroy(); // 确保进程销毁
            }
        }
        return output.toString();
    }

    // 通用流读取方法（指定编码，避免阻塞）
    private void readStream(InputStream inputStream, StringBuilder output, String charset) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, charset))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        } catch (IOException e) {
            output.append("读取流失败：").append(e.getMessage()).append("\n");
        }
    }


}
