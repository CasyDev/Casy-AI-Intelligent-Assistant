package com.casy.casyaiagent.service;

import cn.hutool.crypto.digest.MD5;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 第三方翻译服务（百度翻译API）- 基于Hutool工具类实现
 *
 * @author linlin
 */
@Service
public class BaiduTranslationService {
    @Value("${baidu.translate.appid}")
    private String appId;
    @Value("${baidu.translate.secretKey}")
    private String secretKey;

    // 核心翻译方法：sourceLang=源语言（zh/en），targetLang=目标语言，text=待翻译文本
    public String translate(String sourceLang, String targetLang, String text) {
        // 1. 生成百度翻译API所需参数
        String salt = String.valueOf(System.currentTimeMillis());
        // Hutool MD5生成签名（逻辑不变，保留原有签名规则）
        String sign = MD5.create().digestHex(appId + text + salt + secretKey);
        // 2. 构建请求参数Map（Hutool HttpUtil推荐用Map传参，自动编码）
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("q", text);          // 待翻译文本
        paramMap.put("from", sourceLang); // 源语言
        paramMap.put("to", targetLang);   // 目标语言
        paramMap.put("appid", appId);     // 应用ID
        paramMap.put("salt", salt);       // 随机盐值
        paramMap.put("sign", sign);       // 签名

        // 3. 调用百度翻译API（使用Hutool HttpUtil发起GET请求）
        try {
            // Hutool HttpUtil会自动拼接参数并处理URL编码，避免手动拼接URL的编码问题
            String responseStr = HttpUtil.get("http://api.fanyi.baidu.com/api/trans/vip/translate", paramMap);

            // 4. 用Hutool JsonUtil解析返回结果（替代原Map解析，更简洁）
            JSONObject responseJson = JSONUtil.parseObj(responseStr);
            // 检查是否包含翻译结果字段
            if (responseJson.containsKey("trans_result")) {
                JSONArray transResult = responseJson.getJSONArray("trans_result");
                // 取第一个翻译结果的目标文本
                return transResult.getJSONObject(0).getStr("dst");
            }
        } catch (Exception e) {
            // 降级策略：翻译失败时返回原始文本，避免流程中断
            System.err.println("翻译API调用失败：" + e.getMessage());
        }
        return text; // 降级返回原文本
    }
}