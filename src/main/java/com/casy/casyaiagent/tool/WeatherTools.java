package com.casy.casyaiagent.tool;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.itextpdf.io.exceptions.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.ai.tool.execution.ToolExecutionException;
import uapi.Client;
import uapi.UapiException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author linin
 * @version 1.0
 * @description: 天气查询工具
 * @date 2026/3/12 20:54
 */
public class WeatherTools {

    private static final Logger logger = LoggerFactory.getLogger(WeatherTools.class);
    // API基础地址
    private static final String BASE_URL = "https://uapis.cn";
    // 天气接口路径（对应原misc().getMiscWeather()）
    private static final String WEATHER_API_PATH = "/misc/weather";
    // 你的API密钥（替换为真实值）
    private String API_KEY = "";

    public WeatherTools(String API_KEY) {
        this.API_KEY = API_KEY;
    }

    @Tool(description = "获取某个城市的天气")
    public String getWeather(@ToolParam(description = "要获取天气的城市名称") String city) {
        throw new IOException("IO异常");
//        Client client = new Client("https://uapis.cn", API_KEY);
//        try {
//            Object response = client.misc().getMiscWeather(Map.of("city", "北123京", "adcode", "", "extended", false, "forecast", false, "hourly", false, "minutely", false, "indices", false, "lang", "zh"));
//            logger.info("API响应结果：" + response.toString());
//            return "API响应结果" + response.toString();
//        } catch (UapiException e) {
//            logger.error("API调用失败：" + e.getMessage());
//            return "API调用失败：" + e.getMessage();
//        } catch (Exception e) {
//            logger.error("API call failed: " + e.getMessage());
//            return "API call failed: " + e.getMessage();
//        }
    }

}