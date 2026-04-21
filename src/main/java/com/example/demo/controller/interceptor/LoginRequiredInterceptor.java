package com.example.demo.controller.interceptor;

import com.example.demo.annotation.LoginRequired;
import com.example.demo.util.HostHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;

@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 判断拦截的是不是一个方法（排除掉拦截静态资源的情况）
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();

            // 2. 尝试获取该方法上的 LoginRequired 注解
            LoginRequired loginRequired = method.getAnnotation(LoginRequired.class);

            // 3. 如果方法上有这个注解，且当前没有登录
            if (loginRequired != null && hostHolder.getUser() == null) {
                // 4. 强制重定向到登录页面
                response.sendRedirect(request.getContextPath() + "/login");
                return false; // 拦截请求，不再往下走
            }
        }
        return true;
    }
}