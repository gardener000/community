package com.example.demo.controller.interceptor;

import com.example.demo.entity.LoginTicket;
import com.example.demo.entity.User;
import com.example.demo.service.MessageService;
import com.example.demo.service.UserService;
import com.example.demo.util.CookieUtil; // 稍后我们补一个读取Cookie的工具类
import com.example.demo.util.HostHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;

@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService; // 你需要在 UserService 里补一个 findLoginTicket(String ticket) 方法

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private MessageService messageService;


    // 1. 请求开始前：查出用户并存入 HostHolder
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从 cookie 中获取凭证
        String ticket = com.example.demo.util.CookieUtil.getValue(request, "ticket");

        if (ticket != null) {
            // 查询凭证
            LoginTicket loginTicket = userService.findLoginTicket(ticket);
            // 检查凭证是否有效 (状态为0且没过期)
            if (loginTicket != null && loginTicket.getStatus() == 0 && loginTicket.getExpired().after(new Date())) {
                // 根据凭证查询用户
                User user = userService.findUserById(loginTicket.getUserId());
                // 在本次请求中持有用户数据
                hostHolder.setUser(user);
            }
        }
        return true;
    }

    // 2. 渲染模板前：将用户数据存入 ModelAndView，方便前端直接用
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if (user != null && modelAndView != null) {
            modelAndView.addObject("loginUser", user);
            // 新增：查询当前用户的未读消息总数并存入 model
            int allUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
            modelAndView.addObject("allUnreadCount", allUnreadCount);
        }
    }

    // 3. 请求结束后：清理掉用户数据
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
    }
}