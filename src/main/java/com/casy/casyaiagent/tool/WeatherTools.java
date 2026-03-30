package com.casy.casyaiagent.tool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import uapi.Client;
import uapi.UapiException;

import java.util.Map;

/**
 * @author linin
 * @version 1.0
 * @description: 天气查询工具
 * @date 2026/3/12 20:54
 */
@Slf4j
public class WeatherTools {

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
//        throw new IOException("IO异常");
        Client client = new Client("https://uapis.cn", API_KEY);
        try {
            Object response = client.misc().getMiscWeather(Map.of("city", "北123京", "adcode", "", "extended", false, "forecast", false, "hourly", false, "minutely", false, "indices", false, "lang", "zh"));
            log.info("API响应结果：" + response.toString());
            return "API响应结果" + response.toString();
        } catch (UapiException e) {
            log.error("API调用失败：" + e.getMessage());
            return "API调用失败：" + e.getMessage();
        } catch (Exception e) {
            log.error("API call failed: " + e.getMessage());
            return "API call failed: " + e.getMessage();
        }
    }

}