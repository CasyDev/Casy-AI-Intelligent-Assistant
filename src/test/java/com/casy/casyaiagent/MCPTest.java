package com.casy.casyaiagent;

import com.casy.casyaiagent.ai.LoveApp;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

/**
 * @author linlin
 * @version 1.0
 * @description: MCP测试相关
 * @date 2026/3/25 21:18
 */
@SpringBootTest
public class MCPTest {

    @Resource
    LoveApp loveApp;

    @Test
    void doChatWithMcp() {
        String chatId = UUID.randomUUID().toString();
        // 测试地图 MCP
        String message = "我的另一半居住在上海静安区，请帮我找到 5 公里内合适的约会地点";
        String answer =  loveApp.doChatWithMcp(message, chatId);
    }

}
