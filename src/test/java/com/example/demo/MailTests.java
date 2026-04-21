package com.example.demo;

import com.example.demo.util.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MailTests {

    @Autowired
    private MailClient mailClient;

    @Test
    public void testTextMail() {
        mailClient.sendMail("330110650@qq.com", "TEST", "你好，这是一封来自牛客社区的测试邮件。");
    }

    @Autowired
    private org.thymeleaf.TemplateEngine templateEngine; // 注入模板引擎

    @Test
    public void testHtmlMail() {
        // 1. 创建 Context 对象 (这是 Thymeleaf 专门用来传数据的容器)
        org.thymeleaf.context.Context context = new org.thymeleaf.context.Context();
        // 2. 塞入数据，名字要和 HTML 里的 ${username} 对应
        context.setVariable("username", "sunday");
        context.setVariable("url", "https://www.nowcoder.com");

        // 3. 利用引擎进行渲染 (参数1是模板路径，不需要加.html；参数2是数据容器)
        String content = templateEngine.process("demo/mail/demo", context);

        // 4. 打印一下看看生成的 HTML 源码 (可选)
        System.out.println(content);

        // 5. 调用你之前的工具类发出去
        mailClient.sendMail("330110650@qq.com", "HTML测试邮件", content);
    }
}
