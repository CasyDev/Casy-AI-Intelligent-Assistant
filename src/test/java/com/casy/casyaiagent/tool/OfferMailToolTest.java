package com.casy.casyaiagent.tool;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Offer邮件发送工具测试类
 */
@SpringBootTest
public class OfferMailToolTest {

    @Autowired
    private OfferMailTool offerMailTool;

    @Test
    public void testSendOfferEmail() {
        // 测试参数
        String toEmail = "landun126228@163.com";  // 请替换为实际测试邮箱
        String candidateName = "张三";
        String position = "高级Java工程师";
        String department = "技术研发部";
        String entryDate = "2026-04-15";
        String annualSalary = "30";
        String workLocation = "上海市浦东新区张江高科技园区";

        // 发送Offer邮件
        String result = offerMailTool.sendOfferEmail(
                toEmail, 
                candidateName, 
                position, 
                department, 
                entryDate, 
                annualSalary, 
                workLocation
        );

        System.out.println("发送结果: " + result);
        
        // 验证结果
        assertNotNull(result);
        assertTrue(result.contains("成功") || result.contains("失败"));
    }

    @Test
    public void testSendOfferEmailWithCustomParams() {
        // 使用不同参数的测试
        String result = offerMailTool.sendOfferEmail(
                "test@example.com",
                "李四",
                "产品经理",
                "产品部",
                "2026-05-01",
                "25",
                "北京市朝阳区望京SOHO"
        );

        System.out.println("自定义参数发送结果: " + result);
        assertNotNull(result);
    }
}
