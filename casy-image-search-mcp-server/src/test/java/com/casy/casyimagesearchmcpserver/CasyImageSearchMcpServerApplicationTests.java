package com.casy.casyimagesearchmcpserver;

import com.casy.casyimagesearchmcpserver.tools.ImageSearchTool;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CasyImageSearchMcpServerApplicationTests {
    @Resource
    private ImageSearchTool imageSearchTool;

    @Test
    void searchImage() {
        String result = imageSearchTool.searchImage("computer");
        Assertions.assertNotNull(result);
    }
}