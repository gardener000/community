package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
    // 1. 设置 Cookie 的示例
    @RequestMapping(path = "/cookie/set", method = RequestMethod.GET)
    @ResponseBody
    public String setCookie(jakarta.servlet.http.HttpServletResponse response) {
        // 创建 cookie
        jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie("code", com.example.demo.util.CommunityUtil.generateUUID());
        // 设置生效范围（只在这个路径下发送）
        cookie.setPath("/cookie");
        // 设置生存时间 (单位秒，这里设为10分钟)
        cookie.setMaxAge(60 * 10);
        // 发送给浏览器
        response.addCookie(cookie);

        return "set cookie success";
    }

    // 2. 读取 Cookie 的示例
    @RequestMapping(path = "/cookie/get", method = RequestMethod.GET)
    @ResponseBody
    public String getCookie(@org.springframework.web.bind.annotation.CookieValue("code") String code) {
        System.out.println("从浏览器拿到的Cookie内容是: " + code);
        return "get cookie: " + code;
    }
    // 1. 设置 Session 的示例
    @RequestMapping(path = "/session/set", method = RequestMethod.GET)
    @ResponseBody
    public String setSession(jakarta.servlet.http.HttpSession session) {
        // 像操作 Map 一样存入数据
        session.setAttribute("id", 1);
        session.setAttribute("name", "TestSession");
        return "set session success";
    }

    // 2. 读取 Session 的示例
    @RequestMapping(path = "/session/get", method = RequestMethod.GET)
    @ResponseBody
    public String getSession(jakarta.servlet.http.HttpSession session) {
        System.out.println(session.getAttribute("id"));
        System.out.println(session.getAttribute("name"));
        return "get session: " + session.getAttribute("name");
    }
}