package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AlphaController {

    @RequestMapping("/hello")
    @ResponseBody
    public String sayHello() {
        return "Hello Spring Boot! 恭喜你，环境搭建成功了！";
    }
    @RequestMapping("/view")
    public String getTeacher(org.springframework.ui.Model model) {
        // 1. 准备数据
        java.util.Date date = new java.util.Date();

        // 2. 将数据塞进 Model 对象中（Model 就像一个搬运工，把数据从 Java 搬到 HTML）
        // 第一个参数是变量名，要跟 HTML 里的 ${now} 对应
        model.addAttribute("now", date.toString());

        // 3. 返回模板的路径（不需要加 .html 后缀）
        // Spring 会自动去 templates 目录下找 demo/view.html
        return "/demo/view";
    }
}