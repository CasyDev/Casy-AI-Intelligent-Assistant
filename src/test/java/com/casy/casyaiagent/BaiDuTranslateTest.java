package com.casy.casyaiagent;

import com.casy.casyaiagent.service.BaiduTranslationService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author linlin
 * @version 1.0
 * @description: 测试百度翻译
 * @date 2026/3/9 20:47
 */
@SpringBootTest
public class BaiDuTranslateTest {

     @Resource
     private BaiduTranslationService baiduTranslationService;

     @Test
    void test() {
         String translate = baiduTranslationService.translate("en", "zh", "hello");
         System.out.println(translate);
     }

}
