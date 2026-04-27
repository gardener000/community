package com.example.demo;

import com.example.demo.util.SensitiveFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SensitiveTests {

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void testSensitiveFilter() {
        String text = "这里可以赌博,可以嫖❤娼,可以吸毒,可以开☆票,哈哈哈!";
        text = sensitiveFilter.filter(text);
        System.out.println(text);
        // 预期输出: 这里可以***,可以***,可以***,可以***,哈哈哈!
    }
}