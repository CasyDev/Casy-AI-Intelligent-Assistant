package com.casy.casyaiagent.tool;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.IOException;

/**
 * 网页抓取工具
 * @author linlin
 */
public class WebScrapingTool {

    @Tool(description = "抓取网页内容")
    public String scrapeWebPage(@ToolParam(description = "要抓取网页的网址") String url) {
        try {
            Document doc = Jsoup.connect(url).get();
            return doc.html();
        } catch (IOException e) {
            return "Error scraping web page: " + e.getMessage();
        }
    }
}
