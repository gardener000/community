package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import java.util.Map;

import static com.example.demo.util.CommunityConstant.ACTIVATION_REPEAT;
import static com.example.demo.util.CommunityConstant.ACTIVATION_SUCCESS;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    // 1. 访问注册页面 (GET)

    @RequestMapping(path = "/register", method = RequestMethod.GET)
    public String getRegisterPage() {
        return "demo/site/register"; // 稍后我们在 templates 下建这个文件夹
    }

    // 2. 处理注册数据 (POST)
    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public String register(Model model, User user) {
        // 调用 Service 层的注册方法
        Map<String, Object> map = userService.register(user);

        if (map == null || map.isEmpty()) {
            // 注册成功，跳转到结果页面
            model.addAttribute("msg", "注册成功，我们已经向您的邮箱发送了一封激活邮件，请尽快激活！");
            model.addAttribute("target", "/index"); // 激活后跳转到首页
            return "demo/site/operate_result";
        } else {
            // 注册失败，把错误信息传回给注册页面
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            model.addAttribute("emailMsg", map.get("emailMsg"));
            return "demo/site/register";
        }
    }
    @RequestMapping(path = "/activation/{userId}/{code}", method = RequestMethod.GET)
    public String activation(Model model, @PathVariable("userId") int userId, @PathVariable("code") String code) {
        int result = userService.activation(userId, code);
        if (result == ACTIVATION_SUCCESS) {
            model.addAttribute("msg", "激活成功，您的账号已经可以正常使用了！");
            model.addAttribute("target", "/login"); // 稍后我们实现登录页
        } else if (result == ACTIVATION_REPEAT) {
            model.addAttribute("msg", "无效操作，该账号已经激活过了！");
            model.addAttribute("target", "/demo/index");
        } else {
            model.addAttribute("msg", "激活失败，您提供的激活码不正确！");
            model.addAttribute("target", "/demo/index");
        }
        return "demo/site/operate_result"; // 复用之前那个带倒计时的结果页面
    }

    @Autowired
    private com.google.code.kaptcha.Producer kaptchaProducer;

    @RequestMapping(path = "/kaptcha", method = RequestMethod.GET)
    public void getKaptcha(jakarta.servlet.http.HttpServletResponse response, jakarta.servlet.http.HttpSession session) {
        // 生成验证码文字
        String text = kaptchaProducer.createText();
        // 生成验证码图片
        java.awt.image.BufferedImage image = kaptchaProducer.createImage(text);

        // 将验证码存入 session
        session.setAttribute("kaptcha", text);

        // 将图片输出给浏览器
        response.setContentType("image/png");
        try {
            java.io.OutputStream os = response.getOutputStream();
            javax.imageio.ImageIO.write(image, "png", os);
        } catch (java.io.IOException e) {
            // logger 记录错误
        }
    }
    // 添加这个方法：用于显示登录页面
    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public String getLoginPage() {
        return "demo/site/login";
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public String login(String username, String password, String code, boolean rememberme,
                        Model model, jakarta.servlet.http.HttpSession session, jakarta.servlet.http.HttpServletResponse response) {
        // 1. 检查验证码 (从 Session 拿)
        String kaptcha = (String) session.getAttribute("kaptcha");
        if (StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !kaptcha.equalsIgnoreCase(code)) {
            model.addAttribute("codeMsg", "验证码不正确!");
            return "demo/site/login";
        }

        // 2. 检查账号, 密码
        int expiredSeconds = rememberme ? 3600 * 24 * 10 : 3600; // 勾选记住我则存10天，否则1小时
        Map<String, Object> map = userService.login(username, password, expiredSeconds);

        if (map.containsKey("ticket")) {
            // 登录成功，将 ticket 存入 Cookie
            jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie("ticket", map.get("ticket").toString());
            cookie.setPath("/"); // 全站有效
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);
            return "redirect:/index"; // 重定向到首页
        } else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "demo/site/login";
        }
    }
}