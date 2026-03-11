package com.casy.casyaiagent;

import com.casy.casyaiagent.ai.LoveApp;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest
class LoveAppTest {

    @Resource
    private LoveApp loveApp;

    @Test
    void testChat() {
        String chatId = UUID.randomUUID().toString();
        // 第一轮
        System.out.println("第一轮");
        String message = "你好，我是DD";
        String answer = loveApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
        // 第二轮
        System.out.println("第二轮");
        message = "我想让另一半（Casy）更爱我";
        answer = loveApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
        // 第三轮
        System.out.println("第三轮");
        message = "我的另一半叫什么来着？刚跟你说过，帮我回忆一下";
        answer = loveApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
    }

    @Test
    void doChatWithRag() {
        // 测试
        String chatId = UUID.randomUUID().toString();
        String message = "I'm married, but we haven't been very close since we got married. What should I do?";
        String answer =  loveApp.doChatWithRagForBaiduTranslation(message, chatId, "已婚", true);
        Assertions.assertNotNull(answer);

//        String chatId = UUID.randomUUID().toString();
//        String message = "我已经结婚了，但是婚后关系不太亲密，怎么办？";
//        String answer =  loveApp.doChatWithRagForContextualQuery(message, chatId);
//        Assertions.assertNotNull(answer);

//        String chatId = UUID.randomUUID().toString();
//        String message = "我已经结婚了，但是婚后关系不太亲密，怎么办？";
//        String answer =  loveApp.doChatWithRagForQueryFilterExpression(message, chatId);
//        Assertions.assertNotNull(answer);

//        String chatId = UUID.randomUUID().toString();
//        String message = "我已经结婚了，但是婚后关系不太亲密，怎么办？";
//        String answer =  loveApp.doChatWithRagForQueryRewriter(message, chatId);
//        Assertions.assertNotNull(answer);

//        String chatId = UUID.randomUUID().toString();
//        String message = "我已经结婚了，但是婚后关系不太亲密，怎么办？";
//        String answer =  loveApp.doChatWithRag(message, chatId);
//        Assertions.assertNotNull(answer);


//        message = "我目前处于什么婚因状态？你好好想想，我已经告诉你了";
//        answer =  loveApp.doChatWithRag(message, chatId);
//        Assertions.assertNotNull(answer);

//        String chatId = UUID.randomUUID().toString();
//        String message = "我是白羊座的，我的恋爱对象可能是什么样的？";
//        String answer =  loveApp.doChatWithRag(message, chatId);
//        Assertions.assertNotNull(answer);
    }

    public static void main(String[] args) {
        String chatId = UUID.randomUUID().toString();
        System.out.println("chatId: " + chatId);
    }
}
