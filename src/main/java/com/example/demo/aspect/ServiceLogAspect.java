package com.example.demo.aspect;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@Aspect
public class ServiceLogAspect {

    private static final Logger logger = LoggerFactory.getLogger(ServiceLogAspect.class);

    // 1. 定义切点 (Pointcut)
    // 语法解释：所有的返回值、service包下的所有类、所有方法、所有参数
    @Pointcut("execution(* com.example.demo.service.*.*(..))")
    public void pointcut() {
    }

    // 2. 定义通知 (Advice) - 在方法执行前触发
    @Before("pointcut()")
    public void before(JoinPoint joinPoint) {
        // 用户[1.2.3.4], 在[时间], 访问了[com.example.demo.service.UserService.findUserById].

        // A. 获取用户 IP (通过工具类拿到当前的 Request)
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        // 特别注意：如果是通过定时任务调用的 service，这里 attributes 可能是 null
        if (attributes == null) {
            return;
        }
        HttpServletRequest request = attributes.getRequest();
        String ip = request.getRemoteHost();

        // B. 获取当前时间
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        // C. 获取访问的类名和方法名
        String target = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();

        // D. 打印日志
        logger.info(String.format("用户[%s], 在[%s], 访问了[%s].", ip, now, target));
    }
}